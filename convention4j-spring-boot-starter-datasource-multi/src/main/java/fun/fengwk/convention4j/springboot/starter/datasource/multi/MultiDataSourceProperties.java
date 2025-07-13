package fun.fengwk.convention4j.springboot.starter.datasource.multi;

import fun.fengwk.convention4j.common.util.NullSafe;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Map;

/**
 * @author fengwk
 * @see MultiDataSourceRegister
 */
@ConfigurationProperties(prefix = MultiDataSourceRegister.PREFIX)
@Data
public class MultiDataSourceProperties {

    private Map<String, DataSourceBeanConfig> multi;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @PostConstruct
    public void init() {
        for (Map.Entry<String, DataSourceBeanConfig> entry : NullSafe.of(multi).entrySet()) {
            HotReplaceDataSourceEvent event = new HotReplaceDataSourceEvent(entry.getKey(), entry.getValue());
            applicationEventPublisher.publishEvent(event);
        }
    }

}
