package fun.fengwk.convention4j.spring.cloud.starter.nacos;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.event.NacosDiscoveryInfoChangedEvent;
import com.alibaba.cloud.nacos.util.InetIPv6Utils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.net.*;
import java.util.Enumeration;
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
public class NacosRegistrationIpCorrector implements Runnable {

    /**
     * @see NacosDiscoveryProperties#IPV4
     */
    private static final String IPV4;

    /**
     * @see NacosDiscoveryProperties#IPV6
     */
    private static final String IPV6;

    static {
        try {
            Field ipv4 = NacosDiscoveryProperties.class.getDeclaredField("IPV4");
            ipv4.setAccessible(true);
            IPV4 = (String) ipv4.get(null);
            Field ipv6 = NacosDiscoveryProperties.class.getDeclaredField("IPV6");
            ipv6.setAccessible(true);
            IPV6 = (String) ipv6.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final NacosDiscoveryProperties nacosDiscoveryProperties;
    private final InetIPv6Utils inetIPv6Utils;
    private final InetUtils inetUtils;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final Environment environment;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void init() {
        executorService.scheduleWithFixedDelay(
            this, 1000, 1000, TimeUnit.MILLISECONDS);
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
                String currentIp = resolveIp(currentMetadata);
                if (currentIp != null && !Objects.equals(ip, currentIp)) {
                    nacosDiscoveryProperties.setIp(currentIp);
                    nacosDiscoveryProperties.setMetadata(
                        newMetadata(nacosDiscoveryProperties.getMetadata(), currentMetadata));
                    try {
                        applicationEventPublisher
                            .publishEvent(new NacosDiscoveryInfoChangedEvent(nacosDiscoveryProperties));
                        log.info("Correct nacos registration ip from {} to {}", ip, currentIp);
                    } catch (Exception ex) {
                        log.error("Failed correct nacos registration ip from {} to {}", ip, currentIp, ex);
                    }
                }
            }
        }
    }

    private String resolveIp(Map<String, String> metadata) {
        String ip = null;
        String networkInterface = nacosDiscoveryProperties.getNetworkInterface();
        String ipType = nacosDiscoveryProperties.getIpType();
        if (StringUtils.isEmpty(networkInterface)) {
            if (ipType == null) {
                ip = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
                String ipv6Addr = inetIPv6Utils.findIPv6Address();
                metadata.put(IPV6, ipv6Addr);
                if (ipv6Addr != null) {
                    metadata.put(IPV6, ipv6Addr);
                }
            } else if (IPV4.equalsIgnoreCase(ipType)) {
                ip = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
            } else if (IPV6.equalsIgnoreCase(ipType)) {
                ip = inetIPv6Utils.findIPv6Address();
                if (StringUtils.isEmpty(ip)) {
                    ip = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
                }
            }
        } else {
            try {
                NetworkInterface netInterface = NetworkInterface.getByName(networkInterface);
                if (netInterface != null) {
                    Enumeration<InetAddress> inetAddress = netInterface.getInetAddresses();
                    while (inetAddress.hasMoreElements()) {
                        InetAddress currentAddress = inetAddress.nextElement();
                        if (currentAddress instanceof Inet4Address
                            || currentAddress instanceof Inet6Address
                            && !currentAddress.isLoopbackAddress()) {
                            ip = currentAddress.getHostAddress();
                            break;
                        }
                    }
                }
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }
        }
        return ip;
    }

    private Map<String, String> newMetadata(Map<String, String> metadata, Map<String, String> curMetadata) {
        Map<String, String> newMetadata = new HashMap<>();
        for (Map.Entry<String, String> entry : metadata.entrySet()) {
            if (!Objects.equals(entry.getKey(), IPV6)) {
                newMetadata.put(entry.getKey(), entry.getValue());
            }
        }
        newMetadata.putAll(curMetadata);
        return newMetadata;
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdown();
    }

}
