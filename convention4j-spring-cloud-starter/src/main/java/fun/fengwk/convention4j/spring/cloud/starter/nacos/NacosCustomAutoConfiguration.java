package fun.fengwk.convention4j.spring.cloud.starter.nacos;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.registry.NacosAutoServiceRegistration;
import com.alibaba.cloud.nacos.util.InetIPv6Utils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * @author fengwk
 */
@ConditionalOnClass(NacosDiscoveryProperties.class)
@AutoConfiguration
public class NacosCustomAutoConfiguration {

    @Bean
    public NacosDiscoveryIpResolver nacosDiscoveryIpResolver(
        InetIPv6Utils inetIPv6Utils,
        InetUtils inetUtils) {
        return new NacosDiscoveryIpResolver(inetIPv6Utils, inetUtils);
    }

    @Bean
    public NacosDiscoveryPropertiesIpFixer nacosDiscoveryPropertiesIpFixer(
        NacosDiscoveryIpResolver nacosDiscoveryIpResolver) {
        return new NacosDiscoveryPropertiesIpFixer(nacosDiscoveryIpResolver);
    }

    @Bean
    public NacosRegistrationIpCorrector nacosRegistrationIpCorrector(
        NacosDiscoveryProperties nacosDiscoveryProperties,
        ApplicationEventPublisher applicationEventPublisher,
        Environment environment,
        NacosDiscoveryIpResolver nacosDiscoveryIpResolver) {
        return new NacosRegistrationIpCorrector(nacosDiscoveryProperties,
            applicationEventPublisher, environment, nacosDiscoveryIpResolver);
    }

    @Bean
    public NacosRegistrationOnlineEndpoint nacosRegistrationOnlineEndpoint(
        NacosAutoServiceRegistration nacosAutoServiceRegistration) {
        return new NacosRegistrationOnlineEndpoint(nacosAutoServiceRegistration);
    }

    @Bean
    public NacosRegistrationOfflineEndpoint nacosRegistrationOfflineEndpoint(
        NacosAutoServiceRegistration nacosAutoServiceRegistration) {
        return new NacosRegistrationOfflineEndpoint(nacosAutoServiceRegistration);
    }

}
