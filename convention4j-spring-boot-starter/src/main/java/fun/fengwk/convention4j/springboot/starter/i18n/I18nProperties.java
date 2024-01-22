package fun.fengwk.convention4j.springboot.starter.i18n;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Locale;

/**
 * 
 * @author fengwk
 */
@Data
@ConfigurationProperties(prefix = "convention.i18n")
public class I18nProperties {
    
    /**
     * 名称前缀
     */
    private String baseName;
    
    /**
     * 语言类型
     */
    private Locale locale = Locale.getDefault();
    
}
