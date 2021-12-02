package fun.fengwk.convention.springboot.starter.code;

import fun.fengwk.commons.i18n.PropertiesResourceBundleLoader;
import fun.fengwk.commons.i18n.ResourceBundle;
import fun.fengwk.commons.i18n.ResourceBundleLoader;
import fun.fengwk.commons.i18n.StringManager;
import fun.fengwk.commons.i18n.StringManagerFactory;
import fun.fengwk.convention.api.code.ErrorCodeFactory;
import fun.fengwk.convention.api.code.I18nErrorCodeFactory;
import fun.fengwk.convention.api.code.SimpleErrorCodeFactory;
import fun.fengwk.convention.springboot.starter.i18n.I18nProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * 
 * @author fengwk
 */
@EnableConfigurationProperties(ErrorCodeProperties.class)
@ConditionalOnClass(ErrorCodeFactory.class)
@Configuration(proxyBeanMethods = false)
public class ErrorCodeAutoConfiguration {
    
    private static final Logger LOG = LoggerFactory.getLogger(ErrorCodeAutoConfiguration.class);

    @ConditionalOnClass(StringManagerFactory.class)
    @ConditionalOnProperty(prefix = "convention.error-code", name = { "i18n.base-name", "i18n.locale" })
    @ConditionalOnMissingBean
    @Bean
    public ErrorCodeFactory i18nErrorCodeFactory(ErrorCodeProperties codeProperties) throws IOException {
        I18nProperties i18n = codeProperties.getI18n();
        ResourceBundleLoader loader = new PropertiesResourceBundleLoader();
        ResourceBundle resourceBundle = loader.load(i18n.getBaseName(), i18n.getLocale());
        StringManagerFactory stringManagerFactory = new StringManagerFactory(resourceBundle);
        StringManager stringManager = stringManagerFactory.getStringManager();
        ErrorCodeFactory errorCodeFactory = new I18nErrorCodeFactory(stringManager);

        GlobalErrorCodeFactory.setInstance(errorCodeFactory);

        LOG.info("{} autoconfiguration successfully, i18n.baseName: {}, i18n.locale: {} ",
                I18nErrorCodeFactory.class.getSimpleName(), i18n.getBaseName(), i18n.getLocale());
        
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
