package fun.fengwk.convention4j.springboot.test.starter.discovery;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import reactor.core.publisher.Flux;

/**
 * @author fengwk
 * @see org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientConfiguration.ReactiveSupportConfiguration#healthCheckDiscoveryClientServiceInstanceListSupplier(ConfigurableApplicationContext)
 */
public class TestReactiveDiscoveryClient implements ReactiveDiscoveryClient {

    private final TestDiscoveryClient delegate;

    public TestReactiveDiscoveryClient(TestDiscoveryProperties testDiscoveryProperties) {
        this.delegate = new TestDiscoveryClient(testDiscoveryProperties);
    }

    @Override
    public String description() {
        return TestReactiveDiscoveryClient.class.getName();
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
