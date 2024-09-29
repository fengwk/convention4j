package fun.fengwk.convention4j.springboot.starter.code;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Locale;

/**
 * 
 * @author fengwk
 */
@ConfigurationProperties(prefix = "convention.code")
public class CodeProperties {

    /**
     * 本地化
     */
    private I18n i18n = new I18n();

    public I18n getI18n() {
        return i18n;
    }

    public void setI18n(I18n i18n) {
        this.i18n = i18n;
    }

    public static class I18n {

        /**
         * 语言类型
         */
        private Locale locale = Locale.getDefault();

        public Locale getLocale() {
            return locale;
        }

        public void setLocale(Locale locale) {
            this.locale = locale;
        }

    }

}
