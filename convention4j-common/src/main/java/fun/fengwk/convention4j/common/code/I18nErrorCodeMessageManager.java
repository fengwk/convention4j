package fun.fengwk.convention4j.common.code;

import fun.fengwk.convention4j.api.code.ErrorCode;
import fun.fengwk.convention4j.common.i18n.AggregateResourceBundle;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author fengwk
 */
public class I18nErrorCodeMessageManager extends AbstractErrorCodeMessageManager {

    private static final String BASE_NAME = "error-code";

    private final ResourceBundle resourceBundle;

    public I18nErrorCodeMessageManager(Locale locale, ClassLoader classLoader) {
        this.resourceBundle = ResourceBundle.getBundle(BASE_NAME, locale, classLoader, AggregateResourceBundle.CONTROL);
    }

    @Override
    public String getMessage(ErrorCode errorCode) {
        try {
            return resourceBundle.getString(errorCode.getCode());
        } catch (MissingResourceException ignore) {
            return null;
        }
    }

}
