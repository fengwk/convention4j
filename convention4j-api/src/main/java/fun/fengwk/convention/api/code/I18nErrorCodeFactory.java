package fun.fengwk.convention.api.code;

import fun.fengwk.commons.i18n.StringManager;

import java.util.Objects;

/**
 * 具备本地化能力的编码生产工厂。
 * 
 * @author fengwk
 */
public class I18nErrorCodeFactory implements ErrorCodeFactory {

    private final StringManager stringManager;

    public I18nErrorCodeFactory(StringManager stringManager) {
        this.stringManager = Objects.requireNonNull(stringManager);
    }

    @Override
    public ErrorCode create(String code) {
        return new ImmutableErrorCode(code, stringManager.getString(code));
    }

    @Override
    public ErrorCode create(String code, String message) {
        return new ImmutableErrorCode(code, message);
    }

}
