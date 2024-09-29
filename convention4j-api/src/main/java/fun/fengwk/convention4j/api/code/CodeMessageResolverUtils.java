package fun.fengwk.convention4j.api.code;

/**
 * 错误码信息管理器持有者。
 *
 * @author fengwk
 */
public class CodeMessageResolverUtils {

    private static volatile CodeMessageResolver INSTANCE;

    private CodeMessageResolverUtils() {
    }

    private static CodeMessageResolver getInstance() {
        return INSTANCE;
    }

    public static void setInstance(CodeMessageResolver instance) {
        CodeMessageResolverUtils.INSTANCE = instance;
    }

    public static String resolve(Code code) {
        CodeMessageResolver resolver = getInstance();
        if (resolver == null) {
            return code.getMessage();
        }
        String resolvedMessage = resolver.resolveMessage(code);
        return resolvedMessage == null ? code.getMessage() : resolvedMessage;
    }

    public static String resolve(Code code, Object context) {
        CodeMessageResolver resolver = getInstance();
        if (resolver == null) {
            return code.getMessage();
        }
        String resolvedMessage = resolver.resolveMessage(code, context);
        return resolvedMessage == null ? code.getMessage() : resolvedMessage;
    }

}
