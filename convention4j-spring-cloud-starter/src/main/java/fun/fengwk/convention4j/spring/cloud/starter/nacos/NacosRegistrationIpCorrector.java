package fun.fengwk.convention4j.spring.cloud.starter.nacos;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.event.NacosDiscoveryInfoChangedEvent;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 为Nacos提供节点IP变化时的自动修正功能，
 * 同时也要谨慎节点IP变化，因为会先注销再重新注册，这意味着将短时间下线服务。
 *
 * @author fengwk
 */
@Slf4j
@AllArgsConstructor
public class NacosRegistrationIpCorrector implements Runnable, ApplicationListener<ContextRefreshedEvent> {

    private final NacosDiscoveryProperties nacosDiscoveryProperties;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final Environment environment;
    private final NacosDiscoveryIpResolver nacosDiscoveryIpResolver;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        executorService.scheduleWithFixedDelay(
            this, 5000, 5000, TimeUnit.MILLISECONDS);
        log.info("{} running", getClass().getSimpleName());
    }

    @Override
    public void run() {
        String envIp = environment.getProperty("spring.cloud.nacos.discovery.ip");
        // 仅在没有强制指定IP的情况下自动修正
        if (envIp == null) {
            String ip = nacosDiscoveryProperties.getIp();
            if (ip != null) {
                Map<String, String> currentMetadata = new HashMap<>();
                // @see NacosDiscoveryProperties#init
                try {
                    String currentIp = nacosDiscoveryIpResolver.resolveIp(nacosDiscoveryProperties, currentMetadata);
                    // 处理IPV6地址变化后无法感知的情况
                    // 执行完resolveIp后currentMetadata中将包含当前的IPV6地址，使用原始的metadata和当前的currentMetadata对比
                    // 如果不相等说明有IPV6地址发生变化，也需要重刷新
                    Map<String, String> metadata = nacosDiscoveryProperties.getMetadata();
                    Map<String, String> newMetadata = newMetadata(metadata, currentMetadata);
                    if (!Objects.equals(ip, currentIp) || !Objects.equals(metadata, newMetadata)) {
                        nacosDiscoveryProperties.setIp(currentIp);
                        nacosDiscoveryProperties.setMetadata(newMetadata);
                        try {
                            applicationEventPublisher
                                .publishEvent(new NacosDiscoveryInfoChangedEvent(nacosDiscoveryProperties));
                            log.info("correct nacos registration ip from {} to {}", ip, currentIp);
                        } catch (Exception ex) {
                            log.error("failed correct nacos registration ip from {} to {}", ip, currentIp, ex);
                            // 失败后需要恢复数据
                            // TODO 可能存在实际成功但抛出异常的情况，导致最终本地数据和nacos注册中心数据不一致
                            nacosDiscoveryProperties.setIp(ip);
                            nacosDiscoveryProperties.setMetadata(metadata);
                        }
                    }
                } catch (Exception ex) {
                    log.error("resolve ip error", ex);
                }
            }
        }
    }

    private Map<String, String> newMetadata(Map<String, String> metadata, Map<String, String> curMetadata) {
        Map<String, String> newMetadata = new HashMap<>();
        for (Map.Entry<String, String> entry : metadata.entrySet()) {
            if (!Objects.equals(entry.getKey(), NacosDiscoveryIpResolver.IPV6)) {
                newMetadata.put(entry.getKey(), entry.getValue());
            }
        }
        newMetadata.putAll(curMetadata);
        return newMetadata;
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdown();
        log.info("{} destroy", getClass().getSimpleName());
    }

}
