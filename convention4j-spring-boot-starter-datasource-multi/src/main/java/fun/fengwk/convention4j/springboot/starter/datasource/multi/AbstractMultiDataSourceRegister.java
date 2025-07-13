package fun.fengwk.convention4j.springboot.starter.datasource.multi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

/**
 * 多数据源注册器
 *
 * @author fengwk
 * @see MultiDataSourceProperties
 */
@Slf4j
public abstract class AbstractMultiDataSourceRegister
    // 必须使用ImportBeanDefinitionRegistrar因为执行时机早于BeanDefinitionRegistryPostProcessor
    // 因此可以先于DataSourceInitializationConfiguration完成DataSource定义的注册
    implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    static final String PREFIX = "spring.datasource";

    private Environment environment;

    protected abstract void register(BeanDefinitionRegistry registry, String name, DataSourceBeanConfig config);

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 此时MultiDataSourceProperties尚未初始化，主动bind获取属性
        MultiDataSourceProperties multiDataSourceProperties = Binder.get(environment)
            .bind(PREFIX, MultiDataSourceProperties.class).orElseGet(() -> null);
        if (multiDataSourceProperties == null) {
            log.debug("multi datasource not configured");
            return;
        }

        Map<String, DataSourceBeanConfig> datasource = multiDataSourceProperties.getMulti();
        if (datasource == null) {
            log.debug("multi datasource empty");
            return;
        }

        for (Map.Entry<String, DataSourceBeanConfig> entry : datasource.entrySet()) {
            String name = entry.getKey();
            DataSourceBeanConfig config = entry.getValue();
            register(registry, name, config);
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
