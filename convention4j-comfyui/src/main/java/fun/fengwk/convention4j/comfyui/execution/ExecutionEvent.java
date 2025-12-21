package fun.fengwk.convention4j.comfyui.execution;

import java.util.List;

/**
 * 执行事件（用于Flux流）
 *
 * @author fengwk
 */
public sealed interface ExecutionEvent {

    /**
     * 执行开始事件
     */
    record Started(String promptId) implements ExecutionEvent {}

    /**
     * 节点开始执行事件
     */
    record NodeStarted(String nodeId, String nodeType) implements ExecutionEvent {}

    /**
     * 节点进度事件
     */
    record NodeProgress(String nodeId, int current, int total) implements ExecutionEvent {}

    /**
     * 节点完成事件
     */
    record NodeCompleted(String nodeId) implements ExecutionEvent {}

    /**
     * 节点使用缓存事件
     */
    record NodesCached(List<String> nodeIds) implements ExecutionEvent {}

    /**
     * 执行成功事件（WebSocket通知）
     */
    record ExecutionSucceeded(String promptId) implements ExecutionEvent {}

    /**
     * 执行完成事件（客户端封装结果后）
     */
    record Completed(ExecutionResult result) implements ExecutionEvent {}

    /**
     * 执行错误事件
     */
    record Error(String message, Exception exception) implements ExecutionEvent {}

    /**
     * WebSocket 连接关闭事件（无正常终结信号时发出）
     */
    record ConnectionClosed(String message) implements ExecutionEvent {}
}