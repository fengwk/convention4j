package fun.fengwk.convention4j.oauth2.server.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author fengwk
 */
@Data
@ConfigurationProperties(prefix = "convention.oauth2.server")
public class OAuth2ServerProperties {

    /**
     * 单点登陆cookie存储时间
     */
    private int ssoStoreSeconds = 60 * 60 * 24 * 30;

}
