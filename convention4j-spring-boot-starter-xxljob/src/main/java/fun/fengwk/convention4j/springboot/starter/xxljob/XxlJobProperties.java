package fun.fengwk.convention4j.springboot.starter.xxljob;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;

/**
 * @author fengwk
 */
@ConfigurationProperties(prefix = "convention.xxl-job")
@Data
public class XxlJobProperties {

    /**
     * 必须，xxl job管理服务地址，如http://vps-app-xxl-job:8080
     */
    private String adminAddresses;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 指定执行器内嵌http服务监听的ip，默认0.0.0.0
     */
    private String ip = "0.0.0.0";

    /**
     * 指定执行器内嵌http服务端口，若不指定（为0）将自动检测一个未使用的端口
     */
    private int port;

    /**
     * 日志路径
     */
    private String logPath = "./logs";

    /**
     * 日志保存天数
     */
    private int logRetentionDays = 15;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @PostConstruct
    public void init() {
        XxlJobPropertiesChangedEvent event = new XxlJobPropertiesChangedEvent(this);
        applicationEventPublisher.publishEvent(event);
    }

}
