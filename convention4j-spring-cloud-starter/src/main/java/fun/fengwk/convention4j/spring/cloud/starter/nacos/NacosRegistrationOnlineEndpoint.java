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
@Endpoint(id = "online")
@AllArgsConstructor
public class NacosRegistrationOnlineEndpoint {

    private final NacosAutoServiceRegistration nacosAutoServiceRegistration;

    @WriteOperation
    public EndpointResult online() {
        try {
            nacosAutoServiceRegistration.start();
            return new EndpointResult("online success");
        } catch (Exception ex) {
            log.error("online failed", ex);
            return new EndpointResult("online failed");
        }
    }

}
