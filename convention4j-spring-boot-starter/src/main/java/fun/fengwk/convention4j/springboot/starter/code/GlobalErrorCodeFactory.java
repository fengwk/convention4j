package fun.fengwk.convention4j.springboot.starter.code;

import fun.fengwk.convention4j.api.code.ErrorCode;
import fun.fengwk.convention4j.api.code.ErrorCodeFactory;

/**
 * @author fengwk
 */
public class GlobalErrorCodeFactory {

    private static volatile ErrorCodeFactory instance;

    /**
     * 设置全局ErrorCodeFactory实例。
     *
     * @param errorCodeFactory
     */
    static void setInstance(ErrorCodeFactory errorCodeFactory) {
        instance = errorCodeFactory;
    }

    /**
     * 指定编码与默认消息生产错误码。
     *
     * @param code
     * @return
     */
    public static ErrorCode create(String code) {
        checkState();
        return instance.create(code);
    }

    /**
     * 指定编码与消息生产错误码。
     *
     * @param code
     * @param message
     * @return
     */
    public static ErrorCode create(String code, String message) {
        checkState();
        return instance.create(code, message);
    }

    private static void checkState() {
        if (instance == null) {
            throw new IllegalStateException(
                    String.format("%s has not been initialized", GlobalErrorCodeFactory.class.getSimpleName()));
        }
    }

}
