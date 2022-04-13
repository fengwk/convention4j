package fun.fengwk.convention4j.springboot.starter.i18n;

import fun.fengwk.convention4j.common.i18n.AggregateResourceBundle;
import fun.fengwk.convention4j.common.i18n.StringManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * 
 * @author fengwk
 */
@EnableConfigurationProperties(I18nProperties.class)
@ConditionalOnClass(StringManagerFactory.class)
@ConditionalOnProperty(prefix = "convention.i18n", name = { "base-name", "locale" })
@Configuration
public class I18nAutoConfiguration {
    
    private static final Logger LOG = LoggerFactory.getLogger(I18nAutoConfiguration.class);
    
    @Primary
    @Bean
    public StringManagerFactory stringManagerFactory(I18nProperties properties, ResourceLoader resourceLoader) throws IOException {
        ClassLoader classLoader = resourceLoader.getClassLoader();
        if (classLoader == null) {
            classLoader = ClassUtils.getDefaultClassLoader();
        }

        ResourceBundle resourceBundle = ResourceBundle.getBundle(properties.getBaseName(), properties.getLocale(),
                classLoader, AggregateResourceBundle.CONTROL);
        StringManagerFactory stringManagerFactory = new StringManagerFactory(resourceBundle);

        GlobalStringManagerFactory.setInstance(stringManagerFactory);

        LOG.info("StringManagerFactory autoconfiguration successfully, baseName: {}, locale: {} ", 
                properties.getBaseName(), properties.getLocale());
        
        return stringManagerFactory;
    }
    
}
