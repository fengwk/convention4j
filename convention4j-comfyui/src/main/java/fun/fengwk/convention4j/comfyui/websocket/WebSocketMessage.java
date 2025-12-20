package fun.fengwk.convention4j.comfyui.websocket;

/**
 * WebSocket消息类型
 *
 * @author fengwk
 */
public enum WebSocketMessage {
    STATUS("status"),                     // 状态更新
    PROGRESS("progress"),                 // 进度更新
    EXECUTING("executing"),               // 节点执行中
    EXECUTION_CACHED("execution_cached"), // 节点使用缓存
    EXECUTED("executed"),                 // 节点执行完成
    EXECUTION_START("execution_start"),   // 开始执行
    EXECUTION_ERROR("execution_error"),   // 执行错误
    EXECUTION_SUCCESS("execution_success"), // 执行成功
    PROGRESS_STATE("progress_state"),     // 进度状态
    CRYSTOOLS_MONITOR("crystools.monitor"); // Crystools监控消息

    private final String typeString;

    WebSocketMessage(String typeString) {
        this.typeString = typeString;
    }

    public String getTypeString() {
        return typeString;
    }

    public static WebSocketMessage fromTypeString(String typeStr) {
        if (typeStr == null) {
            return null;
        }
        for (WebSocketMessage type : values()) {
            if (type.typeString.equals(typeStr)) {
                return type;
            }
        }
        return null;
    }
}