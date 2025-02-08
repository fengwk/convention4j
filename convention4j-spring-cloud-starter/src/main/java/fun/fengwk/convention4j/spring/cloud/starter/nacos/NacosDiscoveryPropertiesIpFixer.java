package fun.fengwk.convention4j.spring.cloud.starter.nacos;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;

/**
 * 修复默认的NacosDiscovery初始化方法无法正确判断指定网口后的ip类型问题
 * 例如指定了tailscale0的ipv4，却获取到了ipv6地址
 *
 * @author fengwk
 */
@Slf4j
@AllArgsConstructor
public class NacosDiscoveryPropertiesIpFixer implements InstantiationAwareBeanPostProcessor {

    private final NacosDiscoveryIpResolver nacosDiscoveryIpResolver;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof NacosDiscoveryProperties properties) {
            // 预计算ip
            String ip = nacosDiscoveryIpResolver.resolveIp(properties, properties.getMetadata());
            properties.setIp(ip);
            log.info("fix nacos ip successfully, ip: {}", ip);
        }
        return bean;
    }

}
