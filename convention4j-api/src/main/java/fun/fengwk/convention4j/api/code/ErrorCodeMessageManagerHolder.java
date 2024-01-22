package fun.fengwk.convention4j.api.code;

/**
 * 错误码信息管理器持有者。
 *
 * @author fengwk
 */
public class ErrorCodeMessageManagerHolder {

    private static volatile ErrorCodeMessageResolver instance;

    private ErrorCodeMessageManagerHolder() {}

    public static ErrorCodeMessageResolver getInstance() {
        return instance;
    }

    public static void setInstance(ErrorCodeMessageResolver instance) {
        ErrorCodeMessageManagerHolder.instance = instance;
    }

}
