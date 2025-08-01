package fun.fengwk.convention4j.spring.cloud.starter.mock;

import fun.fengwk.convention4j.common.util.CollectionUtils;
import fun.fengwk.convention4j.common.util.NullSafe;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fengwk
 * @see org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientConfiguration.BlockingSupportConfiguration#discoveryClientServiceInstanceListSupplier(ConfigurableApplicationContext)
 */
public class MockDiscoveryClient implements DiscoveryClient {

    private final SpringCloudMockEnvironmentProperties springCloudMockEnvironmentProperties;

    public MockDiscoveryClient(SpringCloudMockEnvironmentProperties springCloudMockEnvironmentProperties) {
        this.springCloudMockEnvironmentProperties = Objects.requireNonNull(springCloudMockEnvironmentProperties);
    }

    @Override
    public String description() {
        return MockDiscoveryClient.class.getName();
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        Map<String, List<URI>> services = springCloudMockEnvironmentProperties.getServices();
        List<URI> instanceUriList = NullSafe.of(services).get(serviceId);
        if (CollectionUtils.isEmpty(instanceUriList)) {
            return Collections.emptyList();
        }

        return instanceUriList.stream().map(instanceUri -> {
            DefaultServiceInstance instance = new DefaultServiceInstance();
            instance.setUri(instanceUri);
            instance.setServiceId(serviceId);
            instance.setInstanceId(instance.getHost() + ":" + instance.getPort());
            return instance;
        }).collect(Collectors.toList());
    }

    @Override
    public List<String> getServices() {
        Map<String, List<URI>> services = springCloudMockEnvironmentProperties.getServices();
        return new ArrayList<>(NullSafe.of(services).keySet());
    }

}
