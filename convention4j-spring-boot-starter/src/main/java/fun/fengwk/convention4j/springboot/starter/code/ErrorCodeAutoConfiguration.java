package fun.fengwk.convention4j.springboot.starter.code;

import fun.fengwk.convention4j.api.code.ErrorCodeMessageManagerHolder;
import fun.fengwk.convention4j.api.code.ErrorCodeMessageResolver;
import fun.fengwk.convention4j.common.code.I18nErrorCodeMessageResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;

import java.util.Locale;

/**
 * 
 * @author fengwk
 */
@EnableConfigurationProperties(ErrorCodeProperties.class)
@ConditionalOnClass(ErrorCodeMessageResolver.class)
@Configuration
public class ErrorCodeAutoConfiguration {
    
    private static final Logger log = LoggerFactory.getLogger(ErrorCodeAutoConfiguration.class);

    @ConditionalOnMissingBean
    @Bean
    public ErrorCodeMessageResolver errorCodeMessageResolver(ErrorCodeProperties codeProperties, ResourceLoader resourceLoader) {
        ClassLoader classLoader = resourceLoader.getClassLoader();
        if (classLoader == null) {
            classLoader = ClassUtils.getDefaultClassLoader();
        }

        Locale locale = codeProperties.getI18n().getLocale();
        I18nErrorCodeMessageResolver errorCodeMessageResolver = new I18nErrorCodeMessageResolver(
            codeProperties.getI18n().getLocale(), classLoader);

        ErrorCodeMessageManagerHolder.setInstance(errorCodeMessageResolver);

        log.info("{} autoconfiguration successfully, i18n.locale: {} ",
                errorCodeMessageResolver.getClass().getSimpleName(), locale);
        
        return errorCodeMessageResolver;
    }
    
}
