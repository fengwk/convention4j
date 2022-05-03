package fun.fengwk.convention4j.springboot.starter.i18n;

import fun.fengwk.convention4j.common.i18n.AggregateResourceBundle;
import fun.fengwk.convention4j.common.i18n.StringManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * 
 * @author fengwk
 */
@Slf4j
@EnableConfigurationProperties(I18nProperties.class)
@ConditionalOnClass(StringManagerFactory.class)
@ConditionalOnProperty(prefix = "convention.i18n", name = "base-name")
@Configuration
public class I18nAutoConfiguration {
    
    @ConditionalOnMissingBean
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

        log.info("{} autoconfiguration successfully, baseName: {}, locale: {}",
                StringManagerFactory.class.getSimpleName(), properties.getBaseName(), properties.getLocale());

        return stringManagerFactory;
    }
    
}
