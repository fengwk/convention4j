package fun.fengwk.convention4j.comfyui.execution;

import fun.fengwk.convention4j.comfyui.input.InputFile;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.util.List;

/**
 * 执行选项
 *
 * @author fengwk
 */
@Builder
@Getter
public class ExecutionOptions {
    /**
     * 是否随机化种子
     */
    @Builder.Default
    private final boolean randomizeSeed = false;

    /**
     * 输入文件列表
     */
    private final List<InputFile> inputFiles;

    /**
     * 图像节点ID映射（指定图像输入顺序）
     */
    private final List<String> imageNodeIds;

    /**
     * 执行监听器（同步执行时使用）
     */
    private final ExecutionListener listener;

    /**
     * 执行超时时间
     */
    private final Duration timeout;
}