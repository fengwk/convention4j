package fun.fengwk.convention4j.oauth2.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author fengwk
 */
@Data
@ConfigurationProperties(prefix = "convention.oauth2")
public class OAuth2Properties {

    /**
     * 是否自动初始化基础设施，如果该选项被设置为true，
     * 那么基础设施（例如表结构、存储文件、三方资源）将尽可能地尊重该选项，自动完成初始化工作
     */
    private boolean autoInitInfra = false;

    /**
     * 单点登陆cookie最长存储时间
     */
    private int ssoMaxStoreSeconds = 60 * 60 * 24 * 30;

}
