package fun.fengwk.convention4j.api.code;

/**
 * 错误码信息管理器。
 *
 * @author fengwk
 */
public interface ErrorCodeMessageManager {

    /**
     * 获取指定错误码的错误信息。
     *
     * @param errorCode 错误码。
     * @return 错误码信息。
     */
    String getMessage(ErrorCode errorCode);

    /**
     * 格式化消息，如果消息内容中包含${}符号引用的变量将使用ctx进行格式化。
     *
     * @param message 消息内容。
     * @param ctx 格式化使用的上下文。
     * @return 格式化后的消息内容。
     */
    String formatMessage(String message, Object ctx);

}
