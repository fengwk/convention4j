package fun.fengwk.convention4j.spring.cloud.starter.mock;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * @author fengwk
 */
@Data
@ConfigurationProperties(prefix = "convention.mock.discovery")
public class SpringCloudMockEnvironmentProperties {

    /**
     * key: serviceId
     * value: instanceUri List
     */
    private Map<String, List<URI>> services;

}
