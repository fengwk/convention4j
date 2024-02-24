package fun.fengwk.convention4j.spring.cloud.starter.nacos;

import com.alibaba.cloud.nacos.registry.NacosAutoServiceRegistration;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

/**
 * @author fengwk
 */
@Slf4j
@Endpoint(id = "offline")
@AllArgsConstructor
public class NacosRegistrationOfflineEndpoint {

    private final NacosAutoServiceRegistration nacosAutoServiceRegistration;

    @WriteOperation
    public EndpointResult offline() {
        try {
            nacosAutoServiceRegistration.stop();
            return new EndpointResult("offline success");
        } catch (Exception ex) {
            log.error("offline failed", ex);
            return new EndpointResult("offline failed");
        }
    }

}
