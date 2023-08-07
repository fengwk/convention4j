package fun.fengwk.convention4j.api.code;

/**
 * 错误码信息管理器持有者。
 *
 * @author fengwk
 */
public class ErrorCodeMessageManagerHolder {

    private static volatile ErrorCodeMessageManager instance;

    private ErrorCodeMessageManagerHolder() {}

    public static ErrorCodeMessageManager getInstance() {
        return instance;
    }

    public static void setInstance(ErrorCodeMessageManager instance) {
        ErrorCodeMessageManagerHolder.instance = instance;
    }

}
