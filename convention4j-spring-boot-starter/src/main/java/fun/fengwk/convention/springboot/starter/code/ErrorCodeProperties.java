package fun.fengwk.convention.springboot.starter.code;

import fun.fengwk.convention.springboot.starter.i18n.I18nProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 
 * @author fengwk
 */
@ConfigurationProperties(prefix = "convention.error-code")
public class ErrorCodeProperties {

    /**
     * 本地化
     */
    private I18nProperties i18n;

    public I18nProperties getI18n() {
        return i18n;
    }

    public void setI18n(I18nProperties i18n) {
        this.i18n = i18n;
    }

}
