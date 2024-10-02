package fun.fengwk.convention4j.springboot.starter.result;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 发生内部远程调用时不应该返回http错误码，以免处理错误
 *
 * @author fengwk
 */
public class ResultInternalInvokerUtils {

    private static final String HEADER_IGNORE_ERROR_HTTP_STATUS = "X-Ignore-Error-Http-Status";
    private static final String TRUE = "True";

    private ResultInternalInvokerUtils() {
    }

    public static void setIgnoreErrorHttpStatus(BiConsumer<String, String> headerSetter) {
        headerSetter.accept(HEADER_IGNORE_ERROR_HTTP_STATUS, TRUE);
    }

    public static boolean isIgnoreErrorHttpStatus(Function<String, String> headerGetter) {
        return Objects.equals(headerGetter.apply(HEADER_IGNORE_ERROR_HTTP_STATUS), TRUE);
    }

}
