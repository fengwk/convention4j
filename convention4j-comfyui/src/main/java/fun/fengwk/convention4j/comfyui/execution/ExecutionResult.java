package fun.fengwk.convention4j.comfyui.execution;

import fun.fengwk.convention4j.comfyui.output.NodeOutput;
import fun.fengwk.convention4j.comfyui.output.OutputFile;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 工作流执行结果
 *
 * @author fengwk
 */
@Getter
public class ExecutionResult {
    /**
     * 提示ID
     */
    private final String promptId;

    /**
     * 是否执行成功
     */
    private final boolean success;

    /**
     * 错误信息（失败时）
     */
    private final String errorMessage;

    /**
     * 异常（失败时）
     */
    private final Exception exception;

    /**
     * 所有输出文件
     */
    private final List<OutputFile> outputs;

    /**
     * 按节点ID组织的输出
     */
    private final Map<String, NodeOutput> nodeOutputs;

    public ExecutionResult(
            String promptId,
            boolean success,
            String errorMessage,
            Exception exception,
            List<OutputFile> outputs,
            Map<String, NodeOutput> nodeOutputs) {
        this.promptId = promptId;
        this.success = success;
        this.errorMessage = errorMessage;
        this.exception = exception;
        this.outputs = outputs == null
                ? List.of() : Collections.unmodifiableList(new ArrayList<>(outputs));
        this.nodeOutputs = nodeOutputs == null
                ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(nodeOutputs));
    }

    // ==================== 便捷方法 ====================

    /**
     * 获取所有图像输出
     */
    public List<OutputFile> getImages() {
        return outputs.stream().filter(OutputFile::isImage).toList();
    }

    /**
     * 获取所有视频输出
     */
    public List<OutputFile> getVideos() {
        return outputs.stream().filter(OutputFile::isVideo).toList();
    }

    /**
     * 获取所有音频输出
     */
    public List<OutputFile> getAudios() {
        return outputs.stream().filter(OutputFile::isAudio).toList();
    }

    /**
     * 获取指定节点的输出
     */
    public Optional<NodeOutput> getNodeOutput(String nodeId) {
        return Optional.ofNullable(nodeOutputs.get(nodeId));
    }
}