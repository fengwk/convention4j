package fun.fengwk.convention4j.common.code;

import fun.fengwk.convention4j.common.i18n.AggregateResourceBundle;
import fun.fengwk.convention4j.common.i18n.StringManager;
import fun.fengwk.convention4j.common.i18n.StringManagerFactory;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 具备国际化能力的错误码生产工厂。
 * 
 * @author fengwk
 */
public class I18nErrorCodeFactory extends AbstractErrorCodeFactory {

    private static final String BASE_NAME = "error-code";

    private final StringManager stringManager;

    /**
     *
     * @param locale not null
     * @param classLoader not null
     */
    public I18nErrorCodeFactory(Locale locale, ClassLoader classLoader) {
        if (locale == null) {
            throw new NullPointerException("locale cannot be null");
        }
        if (classLoader == null) {
            throw new NullPointerException("classLoader cannot be null");
        }

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
