package fun.fengwk.convention4j.comfyui.execution;

import java.util.List;

/**
 * 执行监听器（用于同步执行时的回调）
 *
 * @author fengwk
 */
public interface ExecutionListener {

    default void onStart(String promptId) {}

    default void onNodeStart(String nodeId, String nodeType) {}

    default void onNodeProgress(String nodeId, int current, int total) {}

    default void onNodeComplete(String nodeId) {}

    default void onNodeCached(List<String> nodeIds) {}

    default void onComplete(ExecutionResult result) {}

    default void onError(String message, Exception exception) {}
}