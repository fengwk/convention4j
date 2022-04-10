package fun.fengwk.convention.springboot.starter.code;

import fun.fengwk.commons.i18n.StringManagerFactory;
import fun.fengwk.convention.api.code.ErrorCodeFactory;
import fun.fengwk.convention.api.code.I18nErrorCodeFactory;
import fun.fengwk.convention.api.code.SimpleErrorCodeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.Locale;

/**
 * 
 * @author fengwk
 */
@EnableConfigurationProperties(ErrorCodeProperties.class)
@ConditionalOnClass(ErrorCodeFactory.class)
@Configuration
public class ErrorCodeAutoConfiguration {
    
    private static final Logger LOG = LoggerFactory.getLogger(ErrorCodeAutoConfiguration.class);

    @ConditionalOnClass(StringManagerFactory.class)
    @ConditionalOnProperty(prefix = "convention.error-code", name = "i18n.locale")
    @ConditionalOnMissingBean
    @Bean
    public ErrorCodeFactory i18nErrorCodeFactory(ErrorCodeProperties codeProperties, ResourceLoader resourceLoader) throws IOException {
        ClassLoader classLoader = resourceLoader.getClassLoader();
        if (classLoader == null) {
            classLoader = ClassUtils.getDefaultClassLoader();
        }

        Locale locale = codeProperties.getI18n().getLocale();
        ErrorCodeFactory errorCodeFactory = new I18nErrorCodeFactory(codeProperties.getI18n().getLocale(), classLoader);

        GlobalErrorCodeFactory.setInstance(errorCodeFactory);

        LOG.info("{} autoconfiguration successfully, i18n.locale: {} ",
                I18nErrorCodeFactory.class.getSimpleName(), locale);
        
        return errorCodeFactory;
    }
    
    @ConditionalOnMissingBean
    @Bean
    public ErrorCodeFactory simpleErrorCodeFactory() {
        ErrorCodeFactory errorCodeFactory = new SimpleErrorCodeFactory();

        GlobalErrorCodeFactory.setInstance(errorCodeFactory);

        LOG.info("{} autoconfiguration successfully", SimpleErrorCodeFactory.class.getSimpleName());

        return errorCodeFactory;
    }
    
}
