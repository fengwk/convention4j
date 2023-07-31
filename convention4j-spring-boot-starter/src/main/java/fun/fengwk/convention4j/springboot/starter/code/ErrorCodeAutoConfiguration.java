package fun.fengwk.convention4j.springboot.starter.code;

import fun.fengwk.convention4j.common.code.ErrorCodeFactory;
import fun.fengwk.convention4j.common.code.GlobalErrorCodeFactory;
import fun.fengwk.convention4j.common.code.I18nErrorCodeFactory;
import fun.fengwk.convention4j.common.code.SimpleErrorCodeFactory;
import fun.fengwk.convention4j.common.i18n.StringManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
    
    private static final Logger log = LoggerFactory.getLogger(ErrorCodeAutoConfiguration.class);

    @ConditionalOnClass(StringManagerFactory.class)
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

        log.info("{} autoconfiguration successfully, i18n.locale: {} ",
                I18nErrorCodeFactory.class.getSimpleName(), locale);
        
        return errorCodeFactory;
    }
    
    @ConditionalOnMissingBean
    @Bean
    public ErrorCodeFactory simpleErrorCodeFactory() {
        ErrorCodeFactory errorCodeFactory = new SimpleErrorCodeFactory();

        GlobalErrorCodeFactory.setInstance(errorCodeFactory);

        log.info("{} autoconfiguration successfully", SimpleErrorCodeFactory.class.getSimpleName());

        return errorCodeFactory;
    }
    
}
