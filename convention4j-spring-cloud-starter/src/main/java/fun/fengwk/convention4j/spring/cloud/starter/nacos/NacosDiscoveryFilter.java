package fun.fengwk.convention4j.spring.cloud.starter.nacos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 修正 Nacos 关闭后 {@code spring.cloud.nacos.discovery.enabled=false} 未正确停止的自动装配类
 *
 * @author fengwk
 */
public class NacosDiscoveryFilter
    implements EnvironmentPostProcessor, AutoConfigurationImportFilter {

    private static final Set<String> EXCLUDE_AUTO_CONFIGURATION_CLASSES;

    static {
        Set<String> excludeAutoConfigurationClasses = new HashSet<>();
        try {
            excludeAutoConfigurationClasses.add(NacosCustomAutoConfiguration.class.getName());
        } catch (Throwable ignore) {}
        EXCLUDE_AUTO_CONFIGURATION_CLASSES = excludeAutoConfigurationClasses;
    }

    private static volatile Boolean DISCOVERY_ENABLED;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        DISCOVERY_ENABLED = environment.getProperty("spring.cloud.nacos.discovery.enabled", Boolean.class);
    }

    @Override
    public boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
        boolean[] result = new boolean[autoConfigurationClasses.length];
        for (int i = 0; i < autoConfigurationClasses.length; i++) {
            if (Objects.equals(DISCOVERY_ENABLED, false) && EXCLUDE_AUTO_CONFIGURATION_CLASSES.contains(autoConfigurationClasses[i])) {
                result[i] = false;
            } else {
                result[i] = true;
            }
        }
        return result;
    }

}
