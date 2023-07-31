package fun.fengwk.convention4j.common.code;

/**
 * @author fengwk
 */
public class GlobalErrorCodeFactory {

    private static volatile ErrorCodeFactory instance = new SimpleErrorCodeFactory();

    private GlobalErrorCodeFactory() {}

    /**
     * 设置全局ErrorCodeFactory实例。
     *
     * @param errorCodeFactory
     */
    public static void setInstance(ErrorCodeFactory instance) {
        GlobalErrorCodeFactory.instance = instance;
    }

    public static ErrorCodeFactory getInstance() {
        return instance;
    }




}
