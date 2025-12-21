package fun.fengwk.convention4j.comfyui.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fun.fengwk.convention4j.comfyui.ComfyUIClient;
import fun.fengwk.convention4j.comfyui.ComfyUIClientOptions;
import fun.fengwk.convention4j.comfyui.ComfyUIConstants;
import fun.fengwk.convention4j.comfyui.exception.ComfyUIException;
import fun.fengwk.convention4j.comfyui.execution.ExecutionEvent;
import fun.fengwk.convention4j.comfyui.execution.ExecutionListener;
import fun.fengwk.convention4j.comfyui.execution.ExecutionOptions;
import fun.fengwk.convention4j.comfyui.execution.ExecutionResult;
import fun.fengwk.convention4j.comfyui.history.HistoryResult;
import fun.fengwk.convention4j.comfyui.input.InputFile;
import fun.fengwk.convention4j.comfyui.input.UploadResult;
import fun.fengwk.convention4j.comfyui.output.NodeOutput;
import fun.fengwk.convention4j.comfyui.output.OutputFile;
import fun.fengwk.convention4j.comfyui.output.OutputType;
import fun.fengwk.convention4j.comfyui.websocket.ComfyUIWebSocket;
import fun.fengwk.convention4j.comfyui.workflow.Workflow;
import fun.fengwk.convention4j.common.http.client.ReactiveHttpClientUtils;
import fun.fengwk.convention4j.common.json.jackson.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 默认ComfyUI客户端实现
 *
 * @author fengwk
 */
@Slf4j
public class DefaultComfyUIClient implements ComfyUIClient {

    private final HttpClient httpClient;
    private final ComfyUIClientOptions options;
    private final String clientId;
    private final ComfyUIWebSocket webSocket;

    public DefaultComfyUIClient(ComfyUIClientOptions options) {
        this.options = options;
        this.httpClient = createHttpClient(options);
        this.clientId = UUID.randomUUID().toString();
        this.webSocket = new ComfyUIWebSocket(httpClient, options.getBaseUrl(), clientId, options.getApiKey(), options.getWebsocketTimeout());
    }

    /**
     * 创建 HttpClient，应用 connectTimeout 配置
     */
    private HttpClient createHttpClient(ComfyUIClientOptions options) {
        if (options.getHttpClient() != null) {
            return options.getHttpClient();
        }
        
        return HttpClient.newBuilder()
                .connectTimeout(options.getConnectTimeout())
                .build();
    }

    /**
     * 构建 HttpRequest.Builder，自动添加 Authorization 头（如果配置了 apiKey）
     */
    private HttpRequest.Builder buildHttpRequest() {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        
        if (options.getApiKey() != null && !options.getApiKey().isEmpty()) {
            builder.header(ComfyUIConstants.HttpHeaders.AUTHORIZATION,
                          ComfyUIConstants.HttpHeaders.BEARER_PREFIX + options.getApiKey());
        }
        
        return builder;
    }

    @Override
    public Mono<ExecutionResult> execute(Workflow workflow) {
        return execute(workflow, ExecutionOptions.builder().build());
    }

    @Override
    public Mono<ExecutionResult> execute(Workflow workflow, ExecutionOptions options) {
        return executeWithEvents(workflow, options)
                .filter(event -> event instanceof ExecutionEvent.Completed
                              || event instanceof ExecutionEvent.Error
                              || event instanceof ExecutionEvent.ConnectionClosed)
                .last()
                .onErrorMap(NoSuchElementException.class, e ->
                    new ComfyUIException(
                        "Workflow execution failed: No completion event received from ComfyUI. " +
                        "Possible causes: ComfyUI server not running, WebSocket connection failed, " +
                        "or workflow terminated unexpectedly."))
                .flatMap(event -> {
                    if (event instanceof ExecutionEvent.Completed completed) {
                        return Mono.just(completed.result());
                    } else if (event instanceof ExecutionEvent.Error error) {
                        return Mono.error(error.exception());
                    } else if (event instanceof ExecutionEvent.ConnectionClosed closed) {
                        return Mono.error(new ComfyUIException(
                            "Workflow execution failed: " + closed.message()));
                    }
                    return Mono.error(new ComfyUIException("Unknown execution result"));
                });
    }

    @Override
    public Flux<ExecutionEvent> executeWithEvents(Workflow workflow) {
        return executeWithEvents(workflow, ExecutionOptions.builder().build());
    }

    @Override
    public Flux<ExecutionEvent> executeWithEvents(Workflow workflow, ExecutionOptions options) {
        // 1. 处理输入文件上传（响应式）
        Mono<Void> uploadMono;
        if (options.getInputFiles() != null && !options.getInputFiles().isEmpty()) {
            uploadMono = Flux.fromIterable(options.getInputFiles())
                    .flatMap(inputFile -> uploadFile(inputFile.getFilename(), inputFile.getData(), inputFile.getMimeType()))
                    .then();
        } else {
            uploadMono = Mono.empty();
        }
        
        Flux<ExecutionEvent> eventFlux = uploadMono
                .then(Mono.fromSupplier(() -> {
                    // 2. 随机种子
                    Workflow finalWorkflow = workflow.copy();
                    if (options.isRandomizeSeed()) {
                        finalWorkflow.randomizeSeed();
                    }

                    // 3. 设置文件输入节点（根据 fileNodeIds 一一对应）
                    if (options.getFileNodeIds() != null && options.getInputFiles() != null) {
                        List<InputFile> files = options.getInputFiles();
                        List<String> nodeIds = options.getFileNodeIds();
                        for (int i = 0; i < Math.min(files.size(), nodeIds.size()); i++) {
                            finalWorkflow.setFileInput(nodeIds.get(i), files.get(i).getFilename());
                        }
                    }
                    return finalWorkflow;
                }))
                .flatMap(this::queuePrompt)
                .flatMapMany(promptId -> {
                    // 4. 监听WebSocket事件
                    return webSocket.getEvents()
                            .filter(event -> isEventForPrompt(event, promptId))
                            .doOnNext(event -> {
                                if (options.getListener() != null) {
                                    handleListenerEvent(event, options.getListener());
                                }
                            })
                            .takeUntil(event -> event instanceof ExecutionEvent.ExecutionSucceeded
                                             || event instanceof ExecutionEvent.Completed
                                             || event instanceof ExecutionEvent.Error)
                            .concatMap(event -> {
                                if (event instanceof ExecutionEvent.ExecutionSucceeded) {
                                    // 获取历史记录和输出
                                    return getHistory(promptId)
                                            .map(history -> createExecutionResult(promptId, history))
                                            .map(ExecutionEvent.Completed::new);
                                }
                                return Mono.just(event);
                            });
                })
                .doOnError(error -> {
                    // 确保异常时记录日志
                    log.error("Error during workflow execution", error);
                });
        
        // 应用执行超时控制
        if (options.getTimeout() != null) {
            eventFlux = eventFlux.timeout(options.getTimeout())
                    .onErrorMap(java.util.concurrent.TimeoutException.class,
                        e -> new fun.fengwk.convention4j.comfyui.exception.ExecutionException(
                            "Workflow execution timeout after " + options.getTimeout()));
        }
        
        return eventFlux;
    }

    /**
     * 检查事件是否属于指定的prompt
     * 注意：当前实现假设单任务模式，对于Started事件验证promptId，其他事件默认接受
     * 如需支持并发任务，需要在ExecutionEvent中为所有事件类型添加promptId字段
     */
    private boolean isEventForPrompt(ExecutionEvent event, String promptId) {
        if (event instanceof ExecutionEvent.Started started) {
            return started.promptId().equals(promptId);
        }
        // 对于其他事件类型，当前简化处理为接受所有事件
        // 这在单任务场景下是安全的，ComfyUI的WebSocket会按顺序推送事件
        return true;
    }
    
    private void handleListenerEvent(ExecutionEvent event, ExecutionListener listener) {
        if (event instanceof ExecutionEvent.Started started) {
            listener.onStart(started.promptId());
        } else if (event instanceof ExecutionEvent.NodeStarted nodeStarted) {
            listener.onNodeStart(nodeStarted.nodeId(), nodeStarted.nodeType());
        } else if (event instanceof ExecutionEvent.NodeProgress nodeProgress) {
            listener.onNodeProgress(nodeProgress.nodeId(), nodeProgress.current(), nodeProgress.total());
        } else if (event instanceof ExecutionEvent.NodeCompleted nodeCompleted) {
            listener.onNodeComplete(nodeCompleted.nodeId());
        } else if (event instanceof ExecutionEvent.NodesCached nodesCached) {
            listener.onNodeCached(nodesCached.nodeIds());
        } else if (event instanceof ExecutionEvent.Completed completed) {
            listener.onComplete(completed.result());
        } else if (event instanceof ExecutionEvent.Error error) {
            listener.onError(error.message(), error.exception());
        } else if (event instanceof ExecutionEvent.ConnectionClosed closed) {
            listener.onError(closed.message(), new ComfyUIException(closed.message()));
        }
    }

    /**
     * 异步提交工作流到队列
     */
    private Mono<String> queuePrompt(Workflow workflow) {
        String url = options.getBaseUrl() + ComfyUIConstants.ApiPaths.PROMPT;
        ObjectNode bodyNode = JsonNodeFactory.instance.objectNode();
        bodyNode.put(ComfyUIConstants.JsonFields.CLIENT_ID, clientId);
        bodyNode.set(ComfyUIConstants.JsonFields.PROMPT, workflow.toJsonNode());
        
        String body = JacksonUtils.writeValueAsString(bodyNode);

        HttpRequest request = buildHttpRequest()
                .uri(URI.create(url))
                .header(ComfyUIConstants.HttpHeaders.CONTENT_TYPE, ComfyUIConstants.HttpHeaders.APPLICATION_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .timeout(options.getReadTimeout())
                .build();

        return ReactiveHttpClientUtils.buildSpec(httpClient, request)
                .bodyToString()
                .send()
                .flatMap(responseBody -> {
                    JsonNode response = JacksonUtils.readTree(responseBody);
                    if (response == null) {
                        return Mono.error(new ComfyUIException("Failed to parse response: " + responseBody));
                    }
                    if (response.has(ComfyUIConstants.JsonFields.ERROR)) {
                        return Mono.error(new ComfyUIException("ComfyUI error: " + responseBody));
                    }
                    return Mono.just(response.path(ComfyUIConstants.JsonFields.PROMPT_ID).asText());
                })
                .onErrorMap(e -> {
                    if (e instanceof ComfyUIException) {
                        return e;
                    }
                    return new ComfyUIException("Failed to queue prompt", e);
                });
    }
    
    /**
     * 异步获取执行历史
     */
    @Override
    public Mono<HistoryResult> getHistory(String promptId) {
        String url = options.getBaseUrl() + ComfyUIConstants.ApiPaths.HISTORY + promptId;
        HttpRequest request = buildHttpRequest()
                .uri(URI.create(url))
                .GET()
                .timeout(options.getReadTimeout())
                .build();

        return ReactiveHttpClientUtils.buildSpec(httpClient, request)
                .bodyToString()
                .send()
                .flatMap(responseBody -> {
                    // 历史记录返回格式: { "prompt_id": { ... } }
                    Map<String, HistoryResult> map = JacksonUtils.readValue(
                            responseBody, new TypeReference<Map<String, HistoryResult>>() {});
                    if (map == null) {
                        return Mono.error(new ComfyUIException("Failed to parse history response: " + responseBody));
                    }
                    return Mono.justOrEmpty(map.get(promptId));
                })
                .onErrorMap(e -> {
                    if (e instanceof ComfyUIException) {
                        return e;
                    }
                    return new ComfyUIException("Failed to get history", e);
                });
    }
    
    private ExecutionResult createExecutionResult(String promptId, HistoryResult history) {
        List<OutputFile> outputFiles = new ArrayList<>();
        Map<String, NodeOutput> nodeOutputs = new HashMap<>();
        
        if (history != null && history.getOutputs() != null) {
            for (Map.Entry<String, JsonNode> entry : history.getOutputs().entrySet()) {
                String nodeId = entry.getKey();
                JsonNode outputData = entry.getValue();
                
                List<OutputFile> nodeFiles = new ArrayList<>();
                
                // Process images
                if (outputData.has(ComfyUIConstants.JsonFields.IMAGES)) {
                    for (JsonNode img : outputData.get(ComfyUIConstants.JsonFields.IMAGES)) {
                        OutputFile outputFile = createOutputFile(img, OutputType.IMAGE);
                        nodeFiles.add(outputFile);
                        outputFiles.add(outputFile);
                    }
                }
                
                // Process video
                if (outputData.has(ComfyUIConstants.JsonFields.VIDEO)) {
                    for (JsonNode video : outputData.get(ComfyUIConstants.JsonFields.VIDEO)) {
                        OutputFile outputFile = createOutputFile(video, OutputType.VIDEO);
                        nodeFiles.add(outputFile);
                        outputFiles.add(outputFile);
                    }
                }

                // Process audio
                if (outputData.has(ComfyUIConstants.JsonFields.AUDIO)) {
                    for (JsonNode audio : outputData.get(ComfyUIConstants.JsonFields.AUDIO)) {
                        OutputFile outputFile = createOutputFile(audio, OutputType.AUDIO);
                        nodeFiles.add(outputFile);
                        outputFiles.add(outputFile);
                    }
                }
                
                nodeOutputs.put(nodeId, new NodeOutput(nodeId, nodeFiles, outputData));
            }
        }
        
        return new ExecutionResult(promptId, true, null, null, outputFiles, nodeOutputs);
    }
    
    private OutputFile createOutputFile(JsonNode fileNode, OutputType type) {
        String filename = fileNode.has(ComfyUIConstants.JsonFields.FILENAME) ? fileNode.get(ComfyUIConstants.JsonFields.FILENAME).asText() : null;
        String subfolder = fileNode.has(ComfyUIConstants.JsonFields.SUBFOLDER) ? fileNode.get(ComfyUIConstants.JsonFields.SUBFOLDER).asText() : "";
        String fileType = fileNode.has(ComfyUIConstants.JsonFields.TYPE) ? fileNode.get(ComfyUIConstants.JsonFields.TYPE).asText() : "output";
        
        return new OutputFile(
                filename,
                subfolder,
                fileType,
                type,
                null // MIME type needs to be inferred or fetched
        );
    }

    @Override
    public Mono<UploadResult> uploadFile(String filename, byte[] data, String mimeType) {
        String boundary = "----WebKitFormBoundary" + UUID.randomUUID().toString().replace("-", "");
        byte[] body = buildMultipartBody(boundary, filename, data, mimeType);
        
        HttpRequest request = buildHttpRequest()
            .uri(URI.create(options.getBaseUrl() + ComfyUIConstants.ApiPaths.UPLOAD_IMAGE))
            .POST(HttpRequest.BodyPublishers.ofByteArray(body))
            .header(ComfyUIConstants.HttpHeaders.CONTENT_TYPE, ComfyUIConstants.HttpHeaders.MULTIPART_FORM_DATA_PREFIX + boundary)
            .timeout(options.getReadTimeout())
            .build();
        
        return ReactiveHttpClientUtils.buildSpec(httpClient, request)
            .send()
            .flatMap(result -> {
                if (!result.is2xx()) {
                    return Mono.error(new ComfyUIException("Upload failed: " + result.getStatusCode()));
                }
                return ReactiveHttpClientUtils.bodyToString(result);
            })
            .map(responseBody -> {
                JsonNode response = JacksonUtils.readTree(responseBody);
                if (response == null) {
                    throw new ComfyUIException("Failed to parse upload response: " + responseBody);
                }
                return new UploadResult(
                    response.path(ComfyUIConstants.JsonFields.NAME).asText(),
                    response.path(ComfyUIConstants.JsonFields.SUBFOLDER).asText(null)
                );
            });
    }

    private byte[] buildMultipartBody(String boundary, String filename, byte[] data, String mimeType) {
        // 简单实现multipart body构建
        String header = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"image\"; filename=\"" + filename + "\"\r\n" +
                "Content-Type: " + mimeType + "\r\n\r\n";
        String footer = "\r\n--" + boundary + "--\r\n";
        
        byte[] headerBytes = header.getBytes(StandardCharsets.UTF_8);
        byte[] footerBytes = footer.getBytes(StandardCharsets.UTF_8);
        
        byte[] body = new byte[headerBytes.length + data.length + footerBytes.length];
        System.arraycopy(headerBytes, 0, body, 0, headerBytes.length);
        System.arraycopy(data, 0, body, headerBytes.length, data.length);
        System.arraycopy(footerBytes, 0, body, headerBytes.length + data.length, footerBytes.length);
        
        return body;
    }

    @Override
    public Mono<byte[]> getFile(String filename, String subfolder, String type) {
        try {
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
            StringBuilder url = new StringBuilder(options.getBaseUrl() + ComfyUIConstants.ApiPaths.VIEW + "?" + ComfyUIConstants.UrlParams.FILENAME + "=" + encodedFilename);
            if (subfolder != null && !subfolder.isEmpty()) {
                url.append("&").append(ComfyUIConstants.UrlParams.SUBFOLDER).append("=").append(URLEncoder.encode(subfolder, StandardCharsets.UTF_8));
            }
            if (type != null && !type.isEmpty()) {
                url.append("&").append(ComfyUIConstants.UrlParams.TYPE).append("=").append(URLEncoder.encode(type, StandardCharsets.UTF_8));
            }
            
            HttpRequest request = buildHttpRequest()
                    .uri(URI.create(url.toString()))
                    .GET()
                    .timeout(options.getReadTimeout())
                    .build();
            
            return ReactiveHttpClientUtils.buildSpec(httpClient, request)
                    .send()
                    .flatMap(result -> {
                        if (!result.is2xx()) {
                            return Mono.error(new ComfyUIException("Failed to get file: " + result.getStatusCode()));
                        }
                        return ReactiveHttpClientUtils.bodyToBytes(result);
                    });
        } catch (Exception e) {
            return Mono.error(new ComfyUIException("Failed to encode URL parameters", e));
        }
    }

    @Override
    public void close() {
        log.debug("Closing ComfyUI client for {}", options.getBaseUrl());
        if (webSocket != null) {
            webSocket.close();
        }
    }
}