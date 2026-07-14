package fun.fengwk.convention4j.comfyui;

import fun.fengwk.convention4j.comfyui.execution.ExecutionEvent;
import fun.fengwk.convention4j.comfyui.execution.ExecutionOptions;
import fun.fengwk.convention4j.comfyui.execution.ExecutionResult;
import fun.fengwk.convention4j.comfyui.history.HistoryResult;
import fun.fengwk.convention4j.comfyui.input.InputFile;
import fun.fengwk.convention4j.comfyui.input.InputType;
import fun.fengwk.convention4j.comfyui.input.UploadResult;
import fun.fengwk.convention4j.comfyui.output.OutputFile;
import fun.fengwk.convention4j.comfyui.workflow.Workflow;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

/**
 * ComfyUI客户端主入口
 *
 * @author fengwk
 */
public interface ComfyUIClient extends Closeable {

    // ==================== 无状态任务操作 ====================

    /**
     * 提交工作流，不等待执行完成
     */
    default Mono<PromptSubmission> submit(Workflow workflow) {
        return Mono.error(new UnsupportedOperationException("submit is not supported by this client"));
    }

    /**
     * 获取任务详情
     */
    default Mono<ComfyUIJob> getJob(String jobId) {
        return Mono.error(new UnsupportedOperationException("getJob is not supported by this client"));
    }

    /**
     * 取消任务
     */
    default Mono<Boolean> cancelJob(String jobId) {
        return Mono.error(new UnsupportedOperationException("cancelJob is not supported by this client"));
    }

    // ==================== 工作流执行 ====================

    /**
     * 异步执行工作流，返回执行结果
     */
    Mono<ExecutionResult> execute(Workflow workflow);

    /**
     * 异步执行工作流，返回执行结果，带选项
     */
    Mono<ExecutionResult> execute(Workflow workflow, ExecutionOptions options);

    /**
     * 异步执行工作流，返回执行事件流
     * 可以实时获取进度信息
     */
    Flux<ExecutionEvent> executeWithEvents(Workflow workflow);

    /**
     * 异步执行工作流，返回执行事件流，带选项
     */
    Flux<ExecutionEvent> executeWithEvents(Workflow workflow, ExecutionOptions options);

    // ==================== 文件操作 ====================

    /**
     * 异步上传文件（支持图像、音频、视频等）
     */
    Mono<UploadResult> uploadFile(String filename, byte[] data, String mimeType);

    /**
     * 异步获取输出文件数据
     *
     * @param filename  文件名
     * @param subfolder 子文件夹
     * @param type      文件夹类型
     * @return 包含文件数据的Mono
     */
    Mono<byte[]> getFile(String filename, String subfolder, String type);

    /**
     * 异步获取输出文件数据（便捷方法）
     *
     * @param outputFile 输出文件元数据
     * @return 包含文件数据的Mono
     */
    default Mono<byte[]> getFile(OutputFile outputFile) {
        if (outputFile == null) {
            return Mono.error(new IllegalArgumentException("outputFile must not be null"));
        }
        return getFile(outputFile.getFilename(), outputFile.getSubfolder(), outputFile.getFolderType());
    }

    // ==================== 便捷方法 ====================
    
    /**
     * 上传文件并自动设置到工作流节点（便捷方法）
     *
     * @param workflow 工作流
     * @param nodeId 节点ID
     * @param filename 文件名
     * @param data 文件数据
     * @param mimeType MIME类型
     * @return 更新后的工作流
     */
    default Mono<Workflow> uploadAndSetFile(Workflow workflow, String nodeId, String filename, byte[] data, String mimeType) {
        try {
            validateWorkflowFileBinding(workflow, nodeId, null);
            validateUploadArguments(filename, data, mimeType);
        } catch (IllegalArgumentException e) {
            return Mono.error(e);
        }
        byte[] dataSnapshot = data.clone();
        return uploadFile(filename, dataSnapshot, mimeType)
                .map(result -> workflow.setFileInput(nodeId, uploadedFilePath(result)));
    }
    
    /**
     * 批量上传文件并自动设置到工作流节点（便捷方法）
     *
     * @param workflow 工作流
     * @param files 输入文件列表（包含文件名、数据和MIME类型）
     * @param nodeIds 节点ID列表（与files一一对应）
     * @return 更新后的工作流
     */
    default Mono<Workflow> uploadAndSetFiles(Workflow workflow, List<InputFile> files, List<String> nodeIds) {
        if (files == null || nodeIds == null || files.size() != nodeIds.size()) {
            return Mono.error(new IllegalArgumentException("files and nodeIds must be non-null and have the same size"));
        }

        List<InputFile> inputFileSnapshot = new ArrayList<>(files.size());
        for (InputFile file : files) {
            if (file == null) {
                inputFileSnapshot.add(null);
            } else {
                byte[] data = file.getData();
                inputFileSnapshot.add(new InputFile(
                        file.getFilename(),
                        data == null ? null : data.clone(),
                        file.getMimeType(),
                        file.getType()));
            }
        }
        List<String> fileNodeIdSnapshot = new ArrayList<>(nodeIds);
        try {
            if (workflow == null) {
                throw new IllegalArgumentException("workflow must not be null");
            }
            for (int i = 0; i < inputFileSnapshot.size(); i++) {
                InputFile file = inputFileSnapshot.get(i);
                if (file == null) {
                    throw new IllegalArgumentException("input file at index " + i + " must not be null");
                }
                if (file.getType() == null) {
                    throw new IllegalArgumentException("input file type at index " + i + " must not be null");
                }
                validateWorkflowFileBinding(workflow, fileNodeIdSnapshot.get(i), file.getType());
                validateUploadArguments(file.getFilename(), file.getData(), file.getMimeType());
            }
        } catch (IllegalArgumentException e) {
            return Mono.error(e);
        }

        return Flux.range(0, inputFileSnapshot.size())
                .concatMap(i -> {
                    InputFile file = inputFileSnapshot.get(i);
                    String nodeId = fileNodeIdSnapshot.get(i);
                    return uploadFile(file.getFilename(), file.getData(), file.getMimeType())
                            .doOnNext(result -> workflow.setFileInput(nodeId, uploadedFilePath(result)));
                })
                .then(Mono.just(workflow));
    }

    private static void validateWorkflowFileBinding(Workflow workflow, String nodeId, InputType inputType) {
        if (workflow == null) {
            throw new IllegalArgumentException("workflow must not be null");
        }
        workflow.validateFileInputBinding(nodeId, inputType);
    }

    private static void validateUploadArguments(String filename, byte[] data, String mimeType) {
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

    private static boolean containsInvalidFilenameCharacter(String filename) {
        return filename.indexOf('"') >= 0 || containsControlCharacter(filename);
    }

    private static boolean containsControlCharacter(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (Character.isISOControl(value.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private static String uploadedFilePath(UploadResult result) {
        String subfolder = result.getSubfolder();
        if (subfolder == null || subfolder.isBlank()) {
            return result.getName();
        }
        return subfolder + "/" + result.getName();
    }

    // ==================== 历史记录 ====================

    /**
     * 异步获取执行历史
     */
    Mono<HistoryResult> getHistory(String promptId);

    // ==================== 生命周期 ====================

    /**
     * 关闭客户端，释放资源。关闭后新的响应式订阅会在本地失败；已经开始的HTTP请求不会被强制取消。
     */
    @Override
    void close();
}
