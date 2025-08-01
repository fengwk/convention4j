package fun.fengwk.convention4j.spring.cloud.starter.mock;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import reactor.core.publisher.Flux;

/**
 * @author fengwk
 * @see org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientConfiguration.ReactiveSupportConfiguration#healthCheckDiscoveryClientServiceInstanceListSupplier(ConfigurableApplicationContext)
 */
public class MockReactiveDiscoveryClient implements ReactiveDiscoveryClient {

    private final MockDiscoveryClient delegate;

    public MockReactiveDiscoveryClient(SpringCloudMockEnvironmentProperties springCloudMockEnvironmentProperties) {
        this.delegate = new MockDiscoveryClient(springCloudMockEnvironmentProperties);
    }

    @Override
    public String description() {
        return MockReactiveDiscoveryClient.class.getName();
    }

    @Override
    public Flux<ServiceInstance> getInstances(String serviceId) {
        return Flux.fromIterable(delegate.getInstances(serviceId));
    }

    @Override
    public Flux<String> getServices() {
        return Flux.fromIterable(delegate.getServices());
    }

}
