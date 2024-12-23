package fun.fengwk.convention4j.springboot.starter.web.env;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于初始化REST默认的环境。
 * 默认环境设计理念：没有选择就是最好的选择。
 *
 * @author fengwk
 */
@Deprecated
public class WebEnvironmentInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

    private static final Logger log = LoggerFactory.getLogger(WebEnvironmentInitializer.class);
    
    private static final String REST_PROPERTIES = "REST_PROPERTIES";

    // GlobalRestExceptionHandler需要通过捕捉NoHandlerFoundException处理404请求，因此需要打开DispatcherServlet的throwExceptionIfNoHandlerFound选项
    // 配置详见DispatcherServletConfiguration与WebMvcProperties
    private static final String THROW_EXCEPTION_IF_NO_HANDLER_FOUND_KEY = "spring.mvc.throwExceptionIfNoHandlerFound";
    private static final boolean THROW_EXCEPTION_IF_NO_HANDLER_FOUND_VALUE = true;

    // 约定静态资源全部存放在/static/**路径下
    // spring.mvc.static-path-pattern表示对某个路径的url path进行拦截，例如拦截/static/**的path
    // spring.web.resources.static-locations表示拦截的url到哪个classpath下查找资源
    // 详见WebMvcAutoConfigurationAdapter#addResourceHandlers
    private static final String STATIC_PATH_PATTERN_KEY = "spring.mvc.static-path-pattern";
    private static final String STATIC_PATH_PATTERN_VALUE = "/**";
    private static final String STATIC_LOCATIONS_KEY = "spring.web.resources.static-locations";
    private static final String STATIC_LOCATIONS_VALUE = "classpath:/static/";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();
        if (!propertySources.contains(REST_PROPERTIES)) {
            doInitialize(propertySources);
        }
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    private void doInitialize(MutablePropertySources propertySources) {
        Map<String, Object> restProperties = new HashMap<>();
        restProperties.put(THROW_EXCEPTION_IF_NO_HANDLER_FOUND_KEY, THROW_EXCEPTION_IF_NO_HANDLER_FOUND_VALUE);
        restProperties.put(STATIC_LOCATIONS_KEY, STATIC_LOCATIONS_VALUE);
        restProperties.put(STATIC_PATH_PATTERN_KEY, STATIC_PATH_PATTERN_VALUE);

        // 使用addLast是为了降低当前初始化器配置的优先级
        // 允许开发者根据实际场景在application.yml中设置自定义配置来覆盖当前规约配置
        propertySources.addLast(new MapPropertySource(REST_PROPERTIES, restProperties));

        log.info("{} already initialized", getClass().getSimpleName());
    }

}
