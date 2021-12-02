package fun.fengwk.convention.springboot.starter.i18n;

import fun.fengwk.commons.i18n.PropertiesResourceBundleLoader;
import fun.fengwk.commons.i18n.ResourceBundle;
import fun.fengwk.commons.i18n.ResourceBundleLoader;
import fun.fengwk.commons.i18n.StringManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;

/**
 * 
 * @author fengwk
 */
@EnableConfigurationProperties(I18nProperties.class)
@ConditionalOnClass(StringManagerFactory.class)
@ConditionalOnProperty(prefix = "convention.i18n", name = { "base-name", "locale" })
@Configuration(proxyBeanMethods = false)
public class I18nAutoConfiguration {
    
    private static final Logger LOG = LoggerFactory.getLogger(I18nAutoConfiguration.class);
    
    @Primary
    @Bean
    public StringManagerFactory stringManagerFactory(I18nProperties properties) throws IOException {
        ResourceBundleLoader loader = new PropertiesResourceBundleLoader();
        ResourceBundle resourceBundle = loader.load(properties.getBaseName(), properties.getLocale());
        StringManagerFactory stringManagerFactory = new StringManagerFactory(resourceBundle);

        GlobalStringManagerFactory.setInstance(stringManagerFactory);

        LOG.info("StringManagerFactory autoconfiguration successfully, baseName: {}, locale: {} ", 
                properties.getBaseName(), properties.getLocale());
        
        return stringManagerFactory;
    }
    
}
