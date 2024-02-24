package fun.fengwk.convention4j.spring.cloud.starter.nacos;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
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
    public NacosRegistrationIpCorrector nacosRegistrationIpCorrector(
        NacosDiscoveryProperties nacosDiscoveryProperties,
        InetIPv6Utils inetIPv6Utils,
        InetUtils inetUtils,
        ApplicationEventPublisher applicationEventPublisher,
        Environment environment) {
        return new NacosRegistrationIpCorrector(nacosDiscoveryProperties,
            inetIPv6Utils, inetUtils, applicationEventPublisher, environment);
    }

}
