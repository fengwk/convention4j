package fun.fengwk.convention4j.comfyui.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fun.fengwk.convention4j.comfyui.ComfyUIClient;
import fun.fengwk.convention4j.comfyui.ComfyUIClientOptions;
import fun.fengwk.convention4j.comfyui.ComfyUIConstants;
import fun.fengwk.convention4j.comfyui.ComfyUIJob;
import fun.fengwk.convention4j.comfyui.PromptSubmission;
import fun.fengwk.convention4j.comfyui.exception.ComfyUIException;
import fun.fengwk.convention4j.comfyui.exception.ExecutionException;
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
import fun.fengwk.convention4j.common.http.HttpUtils;
import fun.fengwk.convention4j.common.http.client.HttpClientFactory;
import fun.fengwk.convention4j.common.http.client.ProxySelectorAdapter;
import fun.fengwk.convention4j.common.json.jackson.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.zip.GZIPInputStream;

/**
 * 默认ComfyUI客户端实现
 *
 * @author fengwk
 */
@Slf4j
public class DefaultComfyUIClient implements ComfyUIClient {

    private static final int HISTORY_READY_MAX_ATTEMPTS = 10;
    private static final Duration HISTORY_READY_RETRY_DELAY = Duration.ofMillis(200);

    private final HttpClient httpClient;
    private final ComfyUIClientOptions options;
    private final String baseUrl;
    private final String clientId;
    private final Set<ComfyUIWebSocket> activeExecutionSockets = ConcurrentHashMap.newKeySet();
    private volatile boolean closed;

    public DefaultComfyUIClient(ComfyUIClientOptions options) {
        validateOptions(options);
        this.options = options;
        this.baseUrl = normalizeBaseUrl(options.getBaseUrl());
        this.httpClient = createHttpClient(options);
        this.clientId = UUID.randomUUID().toString();
    }

    private void validateOptions(ComfyUIClientOptions options) {
        if (options == null) {
            throw new IllegalArgumentException("options must not be null");
        }
        validateRequiredPositiveTimeout("connectTimeout", options.getConnectTimeout());
        validateRequiredPositiveTimeout("readTimeout", options.getReadTimeout());
        validateOptionalPositiveTimeout("websocketTimeout", options.getWebsocketTimeout());
        if (options.getApiKey() != null && containsControlCharacter(options.getApiKey())) {
            throw new IllegalArgumentException("apiKey contains invalid header characters");
        }
    }

    private void validateRequiredPositiveTimeout(String name, Duration timeout) {
        if (timeout == null || timeout.isZero() || timeout.isNegative()) {
            throw new IllegalArgumentException(name + " must be positive");
        }
    }

    private void validateOptionalPositiveTimeout(String name, Duration timeout) {
        if (timeout != null && (timeout.isZero() || timeout.isNegative())) {
            throw new IllegalArgumentException(name + " must be positive when specified");
        }
    }

    /**
     * 创建 HttpClient，应用 connectTimeout 配置
     */
    private HttpClient createHttpClient(ComfyUIClientOptions options) {
        if (options.getHttpClient() != null) {
            return options.getHttpClient();
        }
        
        return HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NEVER)
                .connectTimeout(options.getConnectTimeout())
                .proxy(new ProxySelectorAdapter(HttpClientFactory.getDefaultConfigurableListableProxies()))
                .build();
    }

    /**
     * 构建 HttpRequest.Builder，自动添加 Authorization 头（如果配置了 apiKey）
     */
    private HttpRequest.Builder buildHttpRequest() {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        
        if (options.getApiKey() != null && !options.getApiKey().isBlank()) {
            builder.header(ComfyUIConstants.HttpHeaders.AUTHORIZATION,
                          ComfyUIConstants.HttpHeaders.BEARER_PREFIX + options.getApiKey());
        }
        
        return builder;
    }

    private String normalizeBaseUrl(String configuredBaseUrl) {
        if (configuredBaseUrl == null || configuredBaseUrl.isBlank()) {
            throw new IllegalArgumentException("baseUrl must not be blank");
        }
        String normalized = configuredBaseUrl.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        URI uri;
        try {
            uri = URI.create(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("baseUrl must be a valid HTTP URL", e);
        }
        if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                || uri.getHost() == null
                || uri.getPort() == 0
                || uri.getPort() > 65535
                || uri.getRawUserInfo() != null
                || uri.getRawQuery() != null
                || uri.getRawFragment() != null) {
            throw new IllegalArgumentException(
                    "baseUrl must be an absolute HTTP URL without user-info, query or fragment");
        }
        String normalizedScheme = uri.getScheme().toLowerCase(Locale.ROOT);
        return normalizedScheme + normalized.substring(uri.getScheme().length());
    }

    private ComfyUIWebSocket createExecutionWebSocket(String executionClientId) {
        if (closed) {
            throw new ComfyUIException("ComfyUI client is closed");
        }
        ComfyUIWebSocket webSocket = new ComfyUIWebSocket(
                httpClient,
                baseUrl,
                executionClientId,
                options.getApiKey(),
                options.getWebsocketTimeout());
        activeExecutionSockets.add(webSocket);
        if (closed && activeExecutionSockets.remove(webSocket)) {
            webSocket.close();
            throw new ComfyUIException("ComfyUI client is closed");
        }
        return webSocket;
    }

    @Override
    public Mono<PromptSubmission> submit(Workflow workflow) {
        return submit(workflow, clientId);
    }

    private Mono<PromptSubmission> submit(Workflow workflow, String requestClientId) {
        if (workflow == null) {
            return Mono.error(new IllegalArgumentException("workflow must not be null"));
        }

        String url = baseUrl + ComfyUIConstants.ApiPaths.PROMPT;
        ObjectNode bodyNode = JsonNodeFactory.instance.objectNode();
        bodyNode.put(ComfyUIConstants.JsonFields.CLIENT_ID, requestClientId);
        bodyNode.set(ComfyUIConstants.JsonFields.PROMPT, workflow.toJsonNode());

        HttpRequest request = buildHttpRequest()
                .uri(URI.create(url))
                .header(ComfyUIConstants.HttpHeaders.CONTENT_TYPE, ComfyUIConstants.HttpHeaders.APPLICATION_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(
                        JacksonUtils.writeValueAsString(bodyNode), StandardCharsets.UTF_8))
                .timeout(options.getReadTimeout())
                .build();

        String operation = "Submit workflow";
        return sendJson(request, operation)
                .map(response -> new PromptSubmission(
                        requiredText(response, ComfyUIConstants.JsonFields.PROMPT_ID, operation),
                        requiredNumber(response, ComfyUIConstants.JsonFields.NUMBER, operation),
                        requiredObject(response, ComfyUIConstants.JsonFields.NODE_ERRORS, operation)));
    }

    @Override
    public Mono<ComfyUIJob> getJob(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            return Mono.error(new IllegalArgumentException("jobId must not be blank"));
        }

        String url = baseUrl + ComfyUIConstants.ApiPaths.JOBS + encodePathSegment(jobId);
        HttpRequest request = buildHttpRequest()
                .uri(URI.create(url))
                .GET()
                .timeout(options.getReadTimeout())
                .build();

        String operation = "Get job " + jobId;
        return sendJson(request, operation)
                .map(response -> new ComfyUIJob(
                        requiredText(response, ComfyUIConstants.JsonFields.ID, operation),
                        requiredText(response, ComfyUIConstants.JsonFields.STATUS, operation),
                        optionalNumber(response, ComfyUIConstants.JsonFields.PRIORITY, operation),
                        optionalLong(response, ComfyUIConstants.JsonFields.CREATE_TIME, operation),
                        optionalLong(response, ComfyUIConstants.JsonFields.UPDATE_TIME, operation),
                        optionalText(response, ComfyUIConstants.JsonFields.WORKFLOW_ID, operation),
                        optionalLong(response, ComfyUIConstants.JsonFields.EXECUTION_START_TIME, operation),
                        optionalLong(response, ComfyUIConstants.JsonFields.EXECUTION_END_TIME, operation),
                        optionalInteger(response, ComfyUIConstants.JsonFields.OUTPUTS_COUNT, operation),
                        optionalField(response, ComfyUIConstants.JsonFields.OUTPUTS),
                        optionalField(response, ComfyUIConstants.JsonFields.EXECUTION_ERROR),
                        optionalField(response, ComfyUIConstants.JsonFields.EXECUTION_STATUS),
                        optionalField(response, ComfyUIConstants.JsonFields.WORKFLOW),
                        optionalField(response, ComfyUIConstants.JsonFields.PREVIEW_OUTPUT)));
    }

    @Override
    public Mono<Boolean> cancelJob(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            return Mono.error(new IllegalArgumentException("jobId must not be blank"));
        }

        String url = baseUrl + ComfyUIConstants.ApiPaths.JOBS
                + encodePathSegment(jobId) + ComfyUIConstants.ApiPaths.CANCEL;
        HttpRequest request = buildHttpRequest()
                .uri(URI.create(url))
                .header(ComfyUIConstants.HttpHeaders.CONTENT_TYPE, ComfyUIConstants.HttpHeaders.APPLICATION_JSON)
                .POST(HttpRequest.BodyPublishers.noBody())
                .timeout(options.getReadTimeout())
                .build();

        String operation = "Cancel job " + jobId;
        return sendJson(request, operation)
                .map(response -> requiredBoolean(response, ComfyUIConstants.JsonFields.CANCELLED, operation));
    }

    private Mono<JsonNode> sendJson(HttpRequest request, String operation) {
        return sendBytes(request, operation)
                .flatMap(responseBytes -> {
                    String responseBody = new String(responseBytes, StandardCharsets.UTF_8);
                    try {
                        JsonNode response = JacksonUtils.readTree(responseBody);
                        if (response == null || !response.isObject()) {
                            return Mono.error(malformedResponse(
                                    operation, "expected a JSON object", responseBody));
                        }
                        return Mono.just(response);
                    } catch (Exception e) {
                        return Mono.error(new ComfyUIException(
                                operation + " returned malformed JSON: " + responseBody, e));
                    }
                });
    }

    private Mono<byte[]> sendBytes(HttpRequest request, String operation) {
        return Mono.defer(() -> {
                    ensureOpen();
                    return Mono.fromFuture(httpClient.sendAsync(
                                    request, HttpResponse.BodyHandlers.ofByteArray()))
                            .flatMap(response -> {
                                byte[] responseBody;
                                try {
                                    responseBody = decodeResponseBody(response);
                                } catch (IOException e) {
                                    return Mono.error(new ComfyUIException(
                                            operation + " failed to decode response body", e));
                                }
                                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                                    return Mono.just(responseBody);
                                }
                                return Mono.error(new ComfyUIException(
                                        operation + " failed with HTTP " + response.statusCode()
                                                + ": " + new String(responseBody, StandardCharsets.UTF_8)));
                            });
                })
                .onErrorMap(e -> e instanceof ComfyUIException ? e : new ComfyUIException(operation + " failed", e));
    }

    private void ensureOpen() {
        if (closed) {
            throw new ComfyUIException("ComfyUI client is closed");
        }
    }

    private String requiredText(JsonNode response, String fieldName, String operation) {
        JsonNode field = requiredField(response, fieldName, operation);
        if (!field.isTextual() || field.asText().isBlank()) {
            throw malformedResponse(operation, "field '" + fieldName + "' must be nonblank text", response.toString());
        }
        return field.asText();
    }

    private double requiredNumber(JsonNode response, String fieldName, String operation) {
        JsonNode field = requiredField(response, fieldName, operation);
        if (!field.isNumber()) {
            throw malformedResponse(operation, "field '" + fieldName + "' must be a number", response.toString());
        }
        return field.asDouble();
    }

    private String optionalText(JsonNode response, String fieldName, String operation) {
        JsonNode field = optionalField(response, fieldName);
        if (field == null) {
            return null;
        }
        if (!field.isTextual()) {
            throw malformedResponse(operation, "field '" + fieldName + "' must be text", response.toString());
        }
        return field.asText();
    }

    private Double optionalNumber(JsonNode response, String fieldName, String operation) {
        JsonNode field = optionalField(response, fieldName);
        if (field == null) {
            return null;
        }
        if (!field.isNumber()) {
            throw malformedResponse(operation, "field '" + fieldName + "' must be a number", response.toString());
        }
        return field.asDouble();
    }

    private Long optionalLong(JsonNode response, String fieldName, String operation) {
        JsonNode field = optionalField(response, fieldName);
        if (field == null) {
            return null;
        }
        if (!field.isIntegralNumber() || !field.canConvertToLong()) {
            throw malformedResponse(operation, "field '" + fieldName + "' must be an integer", response.toString());
        }
        return field.asLong();
    }

    private boolean requiredBoolean(JsonNode response, String fieldName, String operation) {
        JsonNode field = requiredField(response, fieldName, operation);
        if (!field.isBoolean()) {
            throw malformedResponse(operation, "field '" + fieldName + "' must be a boolean", response.toString());
        }
        return field.asBoolean();
    }

    private Integer optionalInteger(JsonNode response, String fieldName, String operation) {
        JsonNode field = optionalField(response, fieldName);
        if (field == null) {
            return null;
        }
        if (!field.isIntegralNumber() || !field.canConvertToInt()) {
            throw malformedResponse(operation, "field '" + fieldName + "' must be an integer", response.toString());
        }
        return field.asInt();
    }

    private JsonNode requiredObject(JsonNode response, String fieldName, String operation) {
        JsonNode field = requiredField(response, fieldName, operation);
        if (!field.isObject()) {
            throw malformedResponse(operation, "field '" + fieldName + "' must be an object", response.toString());
        }
        return field;
    }

    private JsonNode requiredField(JsonNode response, String fieldName, String operation) {
        JsonNode field = response.get(fieldName);
        if (field == null || field.isNull()) {
            throw malformedResponse(operation, "missing field '" + fieldName + "'", response.toString());
        }
        return field;
    }

    private JsonNode optionalField(JsonNode response, String fieldName) {
        JsonNode field = response.get(fieldName);
        return field == null || field.isNull() ? null : field;
    }

    private ComfyUIException malformedResponse(String operation, String reason, String responseBody) {
        return new ComfyUIException(operation + " returned malformed response (" + reason + "): " + responseBody);
    }

    private String encodePathSegment(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
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
                .next()
                .switchIfEmpty(Mono.error(new ComfyUIException(
                        "Workflow execution failed: No completion event received from ComfyUI. " +
                        "Possible causes: ComfyUI server not running, WebSocket connection failed, " +
                        "or workflow terminated unexpectedly.")))
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
        if (workflow == null) {
            return Flux.error(new IllegalArgumentException("workflow must not be null"));
        }
        if (options == null) {
            return Flux.error(new IllegalArgumentException("options must not be null"));
        }

        List<InputFile> inputFiles;
        List<String> fileNodeIds;
        Workflow workflowSnapshot;
        try {
            inputFiles = snapshotInputFiles(options.getInputFiles());
            fileNodeIds = options.getFileNodeIds() == null
                    ? List.of() : Collections.unmodifiableList(new ArrayList<>(options.getFileNodeIds()));
            workflowSnapshot = workflow.copy();
            validateExecutionOptions(options);
            validateFileBindings(workflowSnapshot, inputFiles, fileNodeIds);
        } catch (RuntimeException e) {
            return Flux.error(e);
        }

        Flux<ExecutionEvent> eventFlux = Flux.defer(() -> {
            String executionClientId = UUID.randomUUID().toString();
            ComfyUIWebSocket executionWebSocket;
            try {
                executionWebSocket = createExecutionWebSocket(executionClientId);
            } catch (Exception e) {
                return Flux.error(e);
            }

            return executionWebSocket.awaitReady()
                    .then(prepareWorkflow(workflowSnapshot, options, inputFiles, fileNodeIds))
                    .flatMap(finalWorkflow -> submitPreparedWorkflow(
                            finalWorkflow, executionClientId, executionWebSocket))
                    .flatMapMany(submission -> {
                        String promptId = submission.getPromptId();
                        return executionWebSocket.getEvents(promptId)
                                .takeUntil(this::isTerminalExecutionEvent)
                                .concatMap(event -> {
                                    if (event instanceof ExecutionEvent.ExecutionSucceeded) {
                                        return getHistoryAfterExecutionSuccess(promptId)
                                                .map(history -> createExecutionResult(promptId, history))
                                                .map(ExecutionEvent.Completed::new);
                                    }
                                    return Mono.just(event);
                                })
                                .doOnNext(event -> {
                                    if (options.getListener() != null) {
                                        handleListenerEvent(event, options.getListener());
                                    }
                                });
                    })
                    .doOnError(error -> {
                        if (!closed) {
                            log.error("Error during workflow execution", error);
                        }
                    })
                    .doFinally(signalType -> {
                        activeExecutionSockets.remove(executionWebSocket);
                        executionWebSocket.close();
                    });
        });

        if (options.getTimeout() != null) {
            eventFlux = eventFlux.timeout(options.getTimeout())
                    .onErrorMap(TimeoutException.class,
                            e -> new ExecutionException(
                                    "Workflow execution timeout after " + options.getTimeout()));
        }

        return eventFlux;
    }

    private static List<InputFile> snapshotInputFiles(List<InputFile> requestedInputFiles) {
        if (requestedInputFiles == null) {
            return List.of();
        }
        List<InputFile> snapshots = new ArrayList<>(requestedInputFiles.size());
        for (InputFile inputFile : requestedInputFiles) {
            if (inputFile == null) {
                snapshots.add(null);
                continue;
            }
            byte[] data = inputFile.getData();
            snapshots.add(new InputFile(
                    inputFile.getFilename(),
                    data == null ? null : data.clone(),
                    inputFile.getMimeType(),
                    inputFile.getType()));
        }
        return Collections.unmodifiableList(snapshots);
    }

    private void validateExecutionOptions(ExecutionOptions options) {
        Duration timeout = options.getTimeout();
        if (timeout != null && (timeout.isZero() || timeout.isNegative())) {
            throw new IllegalArgumentException("execution timeout must be positive");
        }
    }

    private void validateFileBindings(
            Workflow workflow, List<InputFile> inputFiles, List<String> fileNodeIds) {
        if (inputFiles.size() != fileNodeIds.size()) {
            throw new IllegalArgumentException("inputFiles and fileNodeIds must have the same size");
        }
        for (int i = 0; i < inputFiles.size(); i++) {
            InputFile inputFile = inputFiles.get(i);
            if (inputFile == null) {
                throw new IllegalArgumentException("input file at index " + i + " must not be null");
            }
            if (inputFile.getType() == null) {
                throw new IllegalArgumentException("input file type at index " + i + " must not be null");
            }
            workflow.validateFileInputBinding(fileNodeIds.get(i), inputFile.getType());
            validateUploadArguments(inputFile.getFilename(), inputFile.getData(), inputFile.getMimeType());
        }
    }

    private Mono<PromptSubmission> submitPreparedWorkflow(
            Workflow workflow, String executionClientId, ComfyUIWebSocket executionWebSocket) {
        return Mono.defer(() -> {
            if (closed || executionWebSocket.isClosed()) {
                return Mono.error(new ComfyUIException("ComfyUI client is closed"));
            }
            if (executionWebSocket.isConnectionTerminated()) {
                return Mono.error(new ComfyUIException(
                        "ComfyUI WebSocket connection terminated before workflow submission"));
            }
            return submit(workflow, executionClientId);
        });
    }

    private Mono<Workflow> prepareWorkflow(
            Workflow workflow,
            ExecutionOptions options,
            List<InputFile> inputFiles,
            List<String> fileNodeIds) {
        return Mono.fromSupplier(() -> {
                    Workflow finalWorkflow = workflow.copy();
                    if (options.isRandomizeSeed()) {
                        finalWorkflow.randomizeSeed();
                    }
                    return finalWorkflow;
                })
                .flatMap(finalWorkflow -> inputFiles.isEmpty()
                        ? Mono.just(finalWorkflow)
                        : uploadAndSetFiles(finalWorkflow, inputFiles, fileNodeIds));
    }

    private boolean isTerminalExecutionEvent(ExecutionEvent event) {
        return event instanceof ExecutionEvent.ExecutionSucceeded
                || event instanceof ExecutionEvent.Completed
                || event instanceof ExecutionEvent.Error
                || event instanceof ExecutionEvent.ConnectionClosed;
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
     * 异步获取执行历史
     */
    @Override
    public Mono<HistoryResult> getHistory(String promptId) {
        if (promptId == null || promptId.isBlank()) {
            return Mono.error(new IllegalArgumentException("promptId must not be blank"));
        }
        return getHistoryInternal(promptId)
                .onErrorMap(HistoryNotReadyException.class,
                        error -> new ComfyUIException(error.getMessage(), error));
    }

    private Mono<HistoryResult> getHistoryAfterExecutionSuccess(String promptId) {
        return getHistoryAfterExecutionSuccess(promptId, 1);
    }

    private Mono<HistoryResult> getHistoryAfterExecutionSuccess(String promptId, int attempt) {
        return getHistoryInternal(promptId)
                .onErrorResume(HistoryNotReadyException.class, error -> {
                    if (attempt >= HISTORY_READY_MAX_ATTEMPTS) {
                        return Mono.error(new ComfyUIException(
                                "History for prompt " + promptId
                                        + " was still unavailable after execution succeeded and "
                                        + HISTORY_READY_MAX_ATTEMPTS + " lookup attempts",
                                error));
                    }
                    return Mono.delay(HISTORY_READY_RETRY_DELAY)
                            .then(getHistoryAfterExecutionSuccess(promptId, attempt + 1));
                });
    }

    private Mono<HistoryResult> getHistoryInternal(String promptId) {
        String url = baseUrl + ComfyUIConstants.ApiPaths.HISTORY + encodePathSegment(promptId);
        HttpRequest request = buildHttpRequest()
                .uri(URI.create(url))
                .GET()
                .timeout(options.getReadTimeout())
                .build();

        String operation = "Get history " + promptId;
        return sendJson(request, operation)
                .flatMap(response -> {
                    JsonNode historyNode = response.get(promptId);
                    if (historyNode == null) {
                        return Mono.error(new HistoryNotReadyException(
                                operation + " response did not contain the prompt entry: " + response));
                    }
                    if (!historyNode.isObject()) {
                        return Mono.error(malformedResponse(
                                operation, "prompt entry must be a JSON object", historyNode.toString()));
                    }
                    JsonNode outputsNode = historyNode.get(ComfyUIConstants.JsonFields.OUTPUTS);
                    if (outputsNode == null || !outputsNode.isObject()) {
                        return Mono.error(malformedResponse(
                                operation, "field 'outputs' must be a JSON object", historyNode.toString()));
                    }
                    try {
                        HistoryResult history = JacksonUtils.readValue(historyNode.toString(), HistoryResult.class);
                        if (history == null) {
                            return Mono.error(malformedResponse(
                                    operation, "prompt entry could not be mapped", historyNode.toString()));
                        }
                        return Mono.just(history);
                    } catch (Exception e) {
                        return Mono.error(new ComfyUIException(
                                operation + " returned malformed prompt history: " + historyNode, e));
                    }
                });
    }
    
    private ExecutionResult createExecutionResult(String promptId, HistoryResult history) {
        List<OutputFile> outputFiles = new ArrayList<>();
        Map<String, NodeOutput> nodeOutputs = new LinkedHashMap<>();

        if (history != null && history.getOutputs() != null) {
            for (Map.Entry<String, JsonNode> entry : history.getOutputs().entrySet()) {
                String nodeId = entry.getKey();
                JsonNode outputData = entry.getValue();
                List<OutputFile> nodeFiles = new ArrayList<>();

                if (outputData != null && outputData.isObject()) {
                    addOutputFiles(outputData, ComfyUIConstants.JsonFields.IMAGES, OutputType.IMAGE,
                            nodeFiles, outputFiles);
                    addOutputFiles(outputData, ComfyUIConstants.JsonFields.VIDEO, OutputType.VIDEO,
                            nodeFiles, outputFiles);
                    addOutputFiles(outputData, ComfyUIConstants.JsonFields.VIDEOS, OutputType.VIDEO,
                            nodeFiles, outputFiles);
                    addOutputFiles(outputData, ComfyUIConstants.JsonFields.AUDIO, OutputType.AUDIO,
                            nodeFiles, outputFiles);
                }
                nodeOutputs.put(nodeId, new NodeOutput(nodeId, nodeFiles, outputData));
            }
        }

        return new ExecutionResult(promptId, true, null, null, outputFiles, nodeOutputs);
    }

    private void addOutputFiles(
            JsonNode outputData,
            String fieldName,
            OutputType outputType,
            List<OutputFile> nodeFiles,
            List<OutputFile> outputFiles) {
        JsonNode mediaEntries = outputData.get(fieldName);
        if (mediaEntries == null || !mediaEntries.isArray()) {
            return;
        }
        for (JsonNode mediaEntry : mediaEntries) {
            OutputFile outputFile = createOutputFile(mediaEntry, outputType);
            if (outputFile != null) {
                nodeFiles.add(outputFile);
                outputFiles.add(outputFile);
            }
        }
    }

    private OutputFile createOutputFile(JsonNode fileNode, OutputType type) {
        if (fileNode == null || !fileNode.isObject()) {
            return null;
        }
        JsonNode filenameNode = fileNode.get(ComfyUIConstants.JsonFields.FILENAME);
        if (filenameNode == null || !filenameNode.isTextual() || filenameNode.asText().isBlank()) {
            return null;
        }
        JsonNode subfolderNode = fileNode.get(ComfyUIConstants.JsonFields.SUBFOLDER);
        if (subfolderNode != null && !subfolderNode.isNull() && !subfolderNode.isTextual()) {
            return null;
        }
        JsonNode typeNode = fileNode.get(ComfyUIConstants.JsonFields.TYPE);
        if (typeNode != null && !typeNode.isNull()
                && (!typeNode.isTextual() || typeNode.asText().isBlank())) {
            return null;
        }
        String subfolder = subfolderNode == null || subfolderNode.isNull() ? "" : subfolderNode.asText();
        String fileType = typeNode == null || typeNode.isNull() ? "output" : typeNode.asText();

        return new OutputFile(filenameNode.asText(), subfolder, fileType, type, null);
    }

    @Override
    public Mono<UploadResult> uploadFile(String filename, byte[] data, String mimeType) {
        try {
            validateUploadArguments(filename, data, mimeType);
        } catch (IllegalArgumentException e) {
            return Mono.error(e);
        }

        String boundary = "----WebKitFormBoundary" + UUID.randomUUID().toString().replace("-", "");
        byte[] body = buildMultipartBody(boundary, filename, data, mimeType);
        HttpRequest request = buildHttpRequest()
                .uri(URI.create(baseUrl + ComfyUIConstants.ApiPaths.UPLOAD_IMAGE))
                .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                .header(ComfyUIConstants.HttpHeaders.CONTENT_TYPE,
                        ComfyUIConstants.HttpHeaders.MULTIPART_FORM_DATA_PREFIX + boundary)
                .timeout(options.getReadTimeout())
                .build();

        String operation = "Upload file " + filename;
        return sendJson(request, operation)
                .map(response -> new UploadResult(
                        requiredText(response, ComfyUIConstants.JsonFields.NAME, operation),
                        optionalText(response, ComfyUIConstants.JsonFields.SUBFOLDER, operation)));
    }

    private void validateUploadArguments(String filename, byte[] data, String mimeType) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("filename must not be blank");
        }
        if (containsInvalidFilenameCharacter(filename)) {
            throw new IllegalArgumentException("filename contains invalid multipart header characters");
        }
        if (data == null) {
            throw new IllegalArgumentException("data must not be null");
        }
        if (mimeType == null || mimeType.isBlank()) {
            throw new IllegalArgumentException("mimeType must not be blank");
        }
        if (containsControlCharacter(mimeType)) {
            throw new IllegalArgumentException("mimeType contains invalid header characters");
        }
    }

    private boolean containsInvalidFilenameCharacter(String filename) {
        return filename.indexOf('"') >= 0 || containsControlCharacter(filename);
    }

    private boolean containsControlCharacter(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (Character.isISOControl(value.charAt(i))) {
                return true;
            }
        }
        return false;
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
        if (filename == null || filename.isBlank()) {
            return Mono.error(new IllegalArgumentException("filename must not be blank"));
        }
        String operation = "Get file " + filename;
        return Mono.defer(() -> {
                    ensureOpen();
                    try {
                        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
                        StringBuilder url = new StringBuilder(baseUrl + ComfyUIConstants.ApiPaths.VIEW + "?"
                                + ComfyUIConstants.UrlParams.FILENAME + "=" + encodedFilename);
                        if (subfolder != null && !subfolder.isEmpty()) {
                            url.append("&").append(ComfyUIConstants.UrlParams.SUBFOLDER).append("=")
                                    .append(URLEncoder.encode(subfolder, StandardCharsets.UTF_8));
                        }
                        if (type != null && !type.isEmpty()) {
                            url.append("&").append(ComfyUIConstants.UrlParams.TYPE).append("=")
                                    .append(URLEncoder.encode(type, StandardCharsets.UTF_8));
                        }

                        HttpRequest request = buildHttpRequest()
                                .uri(URI.create(url.toString()))
                                .GET()
                                .timeout(options.getReadTimeout())
                                .build();

                        return sendBytes(request, operation);
                    } catch (Exception e) {
                        return Mono.error(new ComfyUIException(operation + " failed to build request", e));
                    }
                })
                .onErrorMap(e -> e instanceof ComfyUIException ? e : new ComfyUIException(operation + " failed", e));
    }

    private static byte[] decodeResponseBody(HttpResponse<byte[]> response) throws IOException {
        String contentEncoding = response.headers().firstValue("Content-Encoding").orElse(null);
        if (!HttpUtils.gzip(contentEncoding == null ? null : contentEncoding.toLowerCase(Locale.ROOT))) {
            return response.body();
        }
        try (GZIPInputStream input = new GZIPInputStream(new ByteArrayInputStream(response.body()))) {
            return input.readAllBytes();
        }
    }

    @Override
    public void close() {
        log.debug("Closing ComfyUI client for {}", baseUrl);
        closed = true;
        List<ComfyUIWebSocket> sockets = new ArrayList<>(activeExecutionSockets);
        activeExecutionSockets.clear();
        sockets.forEach(ComfyUIWebSocket::close);
    }

    private static class HistoryNotReadyException extends ComfyUIException {

        private HistoryNotReadyException(String message) {
            super(message);
        }
    }
}