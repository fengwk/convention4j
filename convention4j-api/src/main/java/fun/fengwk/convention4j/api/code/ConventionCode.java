package fun.fengwk.convention4j.api.code;

/**
 * 规约编码，在规约中使用HttpStatus作为Status
 *
 * @author fengwk
 */
public interface ConventionCode extends Code, ResolveableCode {

    /**
     * 与http状态码关联
     *
     * @return http状态码
     * @see HttpStatus
     */
    int getStatus();

    @Override
    default ResolvedConventionCode resolve() {
        if (this instanceof ResolvedConventionCode) {
            return (ResolvedConventionCode) this;
        } else if (this instanceof ResolvedCode) {
            return new ImmutableResolvedConventionCode(getStatus(), getCode(), getMessage());
        } else {
            String resolvedMessage = CodeMessageResolverUtils.resolve(this);
            return new ImmutableResolvedConventionCode(getStatus(), getCode(), resolvedMessage);
        }
    }

    @Override
    default ResolvedCode resolve(Object context) {
        String resolvedMessage = CodeMessageResolverUtils.resolve(this, context);
        return new ImmutableResolvedConventionCode(getStatus(), getCode(), resolvedMessage);
    }

    @Override
    default ResolvedCode resolve(String resolvedMessage) {
        return new ImmutableResolvedConventionCode(getStatus(), getCode(), resolvedMessage);
    }

}
