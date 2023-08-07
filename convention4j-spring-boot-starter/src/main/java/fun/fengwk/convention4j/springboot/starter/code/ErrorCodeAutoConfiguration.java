package fun.fengwk.convention4j.springboot.starter.code;

import fun.fengwk.convention4j.api.code.ErrorCodeMessageManager;
import fun.fengwk.convention4j.api.code.ErrorCodeMessageManagerHolder;
import fun.fengwk.convention4j.common.code.I18nErrorCodeMessageManager;
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
@ConditionalOnClass(ErrorCodeMessageManager.class)
@Configuration
public class ErrorCodeAutoConfiguration {
    
    private static final Logger log = LoggerFactory.getLogger(ErrorCodeAutoConfiguration.class);

    @ConditionalOnMissingBean
    @Bean
    public ErrorCodeMessageManager i18nErrorCodeFactory(ErrorCodeProperties codeProperties, ResourceLoader resourceLoader) {
        ClassLoader classLoader = resourceLoader.getClassLoader();
        if (classLoader == null) {
            classLoader = ClassUtils.getDefaultClassLoader();
        }

        Locale locale = codeProperties.getI18n().getLocale();
        I18nErrorCodeMessageManager errorCodeMessageManager = new I18nErrorCodeMessageManager(
            codeProperties.getI18n().getLocale(), classLoader);

        ErrorCodeMessageManagerHolder.setInstance(errorCodeMessageManager);

        log.info("{} autoconfiguration successfully, i18n.locale: {} ",
                errorCodeMessageManager.getClass().getSimpleName(), locale);
        
        return errorCodeMessageManager;
    }
    
}
