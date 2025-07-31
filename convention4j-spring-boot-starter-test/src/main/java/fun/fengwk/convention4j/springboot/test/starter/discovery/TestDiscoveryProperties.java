package fun.fengwk.convention4j.springboot.test.starter.discovery;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * @author fengwk
 */
@Data
@ConfigurationProperties(prefix = "convention.test.discovery")
public class TestDiscoveryProperties {

    /**
     * key: serviceId
     * value: instanceUri List
     */
    private Map<String, List<URI>> services;

}
