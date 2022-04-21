package fun.fengwk.convention4j.api.code;

import fun.fengwk.convention4j.common.i18n.AggregateResourceBundle;
import fun.fengwk.convention4j.common.i18n.StringManager;
import fun.fengwk.convention4j.common.i18n.StringManagerFactory;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * 具备本地化能力的编码生产工厂。
 * 
 * @author fengwk
 */
public class I18nErrorCodeFactory extends ErrorCodeFactory {

    private static final String BASE_NAME = "error-code";

    private final StringManager stringManager;

    /**
     *
     * @param locale not null
     * @param classLoader not null
     * @throws IOException
     */
    public I18nErrorCodeFactory(Locale locale, ClassLoader classLoader) throws IOException {
        Objects.requireNonNull(locale);
        Objects.requireNonNull(classLoader);
        ResourceBundle resourceBundle = ResourceBundle.getBundle(BASE_NAME, locale, classLoader, AggregateResourceBundle.CONTROL);
        StringManagerFactory stringManagerFactory = new StringManagerFactory(resourceBundle);
        this.stringManager = stringManagerFactory.getStringManager();
    }

    @Override
    protected ErrorCode doCreate(String errorCode, Map<String, ?> errors) {
        return new ImmutableErrorCode(errorCode, stringManager.getString(errorCode, errors), errors);
    }

    @Override
    protected ErrorCode doCreate(String errorCode, String message, Map<String, ?> errors) {
        return new ImmutableErrorCode(errorCode, message, errors);
    }

}
