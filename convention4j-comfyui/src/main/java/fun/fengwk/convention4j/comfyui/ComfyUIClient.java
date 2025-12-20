package fun.fengwk.convention4j.comfyui;

import fun.fengwk.convention4j.comfyui.execution.ExecutionEvent;
import fun.fengwk.convention4j.comfyui.execution.ExecutionOptions;
import fun.fengwk.convention4j.comfyui.execution.ExecutionResult;
import fun.fengwk.convention4j.comfyui.history.HistoryResult;
import fun.fengwk.convention4j.comfyui.input.UploadResult;
import fun.fengwk.convention4j.comfyui.output.OutputFile;
import fun.fengwk.convention4j.comfyui.workflow.Workflow;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Closeable;

/**
 * ComfyUI客户端主入口
 *
 * @author fengwk
 */
public interface ComfyUIClient extends Closeable {

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
     * 异步上传图像
     */
    Mono<UploadResult> uploadImage(String filename, byte[] data, String mimeType);

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
        return getFile(outputFile.getFilename(), outputFile.getSubfolder(), outputFile.getFolderType());
    }

    // ==================== 历史记录 ====================

    /**
     * 异步获取执行历史
     */
    Mono<HistoryResult> getHistory(String promptId);

    // ==================== 生命周期 ====================

    /**
     * 关闭客户端，释放资源
     */
    @Override
    void close();
}
