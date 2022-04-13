package fun.fengwk.convention4j.springboot.starter.i18n;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Locale;

/**
 * 
 * @author fengwk
 */
@ConfigurationProperties(prefix = "convention.i18n")
public class I18nProperties {
    
    /**
     * 名称前缀
     */
    private String baseName;
    
    /**
     * 语言类型
     */
    private Locale locale;
    
    public String getBaseName() {
        return baseName;
    }
    
    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }
    
    public Locale getLocale() {
        return locale;
    }
    
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

}
