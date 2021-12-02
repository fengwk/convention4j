package fun.fengwk.convention.api.code;

/**
 * 
 * @author fengwk
 */
public class SimpleErrorCodeFactory implements ErrorCodeFactory {

    @Override
    public ErrorCode create(String code) {
        return new ImmutableErrorCode(code, null);
    }

    @Override
    public ErrorCode create(String code, String message) {
        return new ImmutableErrorCode(code, message);
    }

}
