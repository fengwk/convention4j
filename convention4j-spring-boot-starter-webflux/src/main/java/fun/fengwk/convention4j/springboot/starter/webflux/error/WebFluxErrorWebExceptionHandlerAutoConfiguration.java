package fun.fengwk.convention4j.springboot.starter.webflux.error;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.stream.Collectors;

/**
 * @author fengwk
 */
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
// 必须在ErrorWebFluxAutoConfiguration之前注入
@AutoConfiguration(before = ErrorWebFluxAutoConfiguration.class)
public class WebFluxErrorWebExceptionHandlerAutoConfiguration {

    @Bean
    public WebFluxErrorWebExceptionHandler webFluxErrorWebExceptionHandler(
        ErrorAttributes errorAttributes, ApplicationContext applicationContext,
        ServerCodecConfigurer serverCodecConfigurer, ObjectProvider<ViewResolver> viewResolvers) {

        WebFluxErrorWebExceptionHandler customErrorWebExceptionHandler = new WebFluxErrorWebExceptionHandler(
            errorAttributes, new WebProperties.Resources(), applicationContext);

        customErrorWebExceptionHandler
            .setViewResolvers(viewResolvers.orderedStream().collect(Collectors.toList()));
        customErrorWebExceptionHandler.setMessageWriters(serverCodecConfigurer.getWriters());
        customErrorWebExceptionHandler.setMessageReaders(serverCodecConfigurer.getReaders());

        return customErrorWebExceptionHandler;
    }

}
