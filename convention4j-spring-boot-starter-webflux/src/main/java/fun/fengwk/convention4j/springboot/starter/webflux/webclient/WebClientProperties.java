package fun.fengwk.convention4j.springboot.starter.webflux.webclient;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author fengwk
 */
@Data
@ConfigurationProperties(prefix = "convention4j.webflux.web-client")
public class WebClientProperties {

    /**
     * 连接超时
     */
    private int connectTimeout = 1000;

    /**
     * 整体响应超时
     */
    private Duration responseTimeout = Duration.ofMillis(5000);

}
