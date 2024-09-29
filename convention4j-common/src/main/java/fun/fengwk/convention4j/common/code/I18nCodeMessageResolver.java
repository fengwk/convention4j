package fun.fengwk.convention4j.common.code;

import fun.fengwk.convention4j.api.code.Code;
import fun.fengwk.convention4j.common.i18n.AggregateResourceBundle;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author fengwk
 */
public class I18nCodeMessageResolver extends AbstractCodeMessageResolver {

    private static final String BASE_NAME = "convention-codes";

    private final ResourceBundle resourceBundle;

    public I18nCodeMessageResolver(Locale locale, ClassLoader classLoader) {
        this.resourceBundle = ResourceBundle.getBundle(BASE_NAME, locale, classLoader, AggregateResourceBundle.CONTROL);
    }

    @Override
    public String resolveMessage(Code code) {
        try {
            return resourceBundle.getString(code.getCode());
        } catch (MissingResourceException ignore) {
            return null;
        }
    }

}
