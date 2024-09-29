package fun.fengwk.convention4j.springboot.starter.code;

import fun.fengwk.convention4j.api.code.CodeMessageResolver;
import fun.fengwk.convention4j.api.code.CodeMessageResolverUtils;
import fun.fengwk.convention4j.common.code.I18nCodeMessageResolver;
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
@EnableConfigurationProperties(CodeProperties.class)
@ConditionalOnClass(CodeMessageResolver.class)
@Configuration
public class CodeAutoConfiguration {
    
    private static final Logger log = LoggerFactory.getLogger(CodeAutoConfiguration.class);

    @ConditionalOnMissingBean
    @Bean
    public CodeMessageResolver codeMessageResolver(CodeProperties codeProperties, ResourceLoader resourceLoader) {
        ClassLoader classLoader = resourceLoader.getClassLoader();
        if (classLoader == null) {
            classLoader = ClassUtils.getDefaultClassLoader();
        }

        Locale locale = codeProperties.getI18n().getLocale();
        CodeMessageResolver codeMessageResolver = new I18nCodeMessageResolver(
            codeProperties.getI18n().getLocale(), classLoader);

        CodeMessageResolverUtils.setInstance(codeMessageResolver);

        log.info("{} autoconfiguration successfully, i18n.locale: {} ",
                codeMessageResolver.getClass().getSimpleName(), locale);
        
        return codeMessageResolver;
    }
    
}
