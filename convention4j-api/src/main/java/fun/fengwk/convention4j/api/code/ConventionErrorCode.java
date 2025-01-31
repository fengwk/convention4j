package fun.fengwk.convention4j.api.code;

import java.util.Map;

/**
 * 规约编码，在规约中使用HttpStatus作为Status
 *
 * @author fengwk
 */
public interface ConventionErrorCode extends ConventionCode, ErrorCode {

    @Override
    default ResolvedConventionErrorCode resolve() {
        if (this instanceof ResolvedConventionErrorCode) {
            return (ResolvedConventionErrorCode) this;
        } else if (this instanceof ResolvedCode) {
            return new ImmutableResolvedConventionErrorCode(getStatus(), getCode(), getMessage(), getErrorContext());
        } else {
            String resolvedMessage = CodeMessageResolverUtils.resolve(this);
            return new ImmutableResolvedConventionErrorCode(getStatus(), getCode(), resolvedMessage, getErrorContext());
        }
    }

    @Override
    default ResolvedConventionErrorCode resolve(Object context) {
        String resolvedMessage = CodeMessageResolverUtils.resolve(this, context);
        return new ImmutableResolvedConventionErrorCode(getStatus(), getCode(), resolvedMessage, getErrorContext());
    }

    @Override
    default ResolvedConventionErrorCode resolve(String resolvedMessage) {
        return new ImmutableResolvedConventionErrorCode(getStatus(), getCode(), resolvedMessage, getErrorContext());
    }

    @Override
    default ThrowableConventionErrorCode asThrowable() {
        if (this instanceof ThrowableConventionErrorCode) {
            return (ThrowableConventionErrorCode) this;
        }
        return new ThrowableConventionErrorCode(resolve());
    }

    @Override
    default ThrowableConventionErrorCode asThrowable(Object context) {
        if (this instanceof ThrowableConventionErrorCode) {
            return (ThrowableConventionErrorCode) this;
        }
        return new ThrowableConventionErrorCode(resolve(context));
    }

    @Override
    default ThrowableConventionErrorCode asThrowable(Throwable cause) {
        if (this instanceof ThrowableConventionErrorCode) {
            return (ThrowableConventionErrorCode) this;
        }
        return new ThrowableConventionErrorCode(resolve(), cause);
    }

    @Override
    default ThrowableConventionErrorCode asThrowable(Throwable cause, Object context) {
        if (this instanceof ThrowableConventionErrorCode) {
            return (ThrowableConventionErrorCode) this;
        }
        return new ThrowableConventionErrorCode(resolve(context), cause);
    }

    default ThrowableConventionErrorCode asThrowable(String resolvedMessage) {
        if (this instanceof ThrowableConventionErrorCode) {
            return (ThrowableConventionErrorCode) this;
        }
        return new ThrowableConventionErrorCode(resolve(resolvedMessage));
    }

    default ThrowableConventionErrorCode asThrowable(Throwable cause, String resolvedMessage) {
        if (this instanceof ThrowableConventionErrorCode) {
            return (ThrowableConventionErrorCode) this;
        }
        return new ThrowableConventionErrorCode(resolve(resolvedMessage), cause);
    }

    @Override
    default ConventionErrorCode withErrorContext(Map<String, Object> errorContext) {
        if (this instanceof ResolvedCode) {
            return new ImmutableResolvedConventionErrorCode(getStatus(), getCode(), getMessage(), errorContext);
        } else {
            return new ImmutableConventionErrorCode(getStatus(), getCode(), getMessage(), errorContext);
        }
    }

}