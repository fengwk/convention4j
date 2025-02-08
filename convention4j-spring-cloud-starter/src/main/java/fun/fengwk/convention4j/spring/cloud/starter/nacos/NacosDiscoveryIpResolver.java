package fun.fengwk.convention4j.spring.cloud.starter.nacos;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.util.InetIPv6Utils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.net.*;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author fengwk
 */
@Slf4j
@AllArgsConstructor
public class NacosDiscoveryIpResolver {

    /**
     * @see NacosDiscoveryProperties#IPV4
     */
    static final String IPV4;

    /**
     * @see NacosDiscoveryProperties#IPV6
     */
    static final String IPV6;

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

    private final InetIPv6Utils inetIPv6Utils;
    private final InetUtils inetUtils;
    private final InetUtilsProperties properties;

    public String resolveIp(NacosDiscoveryProperties nacosDiscoveryProperties, Map<String, String> metadata) {
        String ip;
        String ipType = nacosDiscoveryProperties.getIpType();
        // traversing network interfaces if didn't specify an interface
        String networkInterface = nacosDiscoveryProperties.getNetworkInterface();
        if (StringUtils.isEmpty(networkInterface)) {
            if (ipType == null) {
                ip = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
                String ipv6Addr = inetIPv6Utils.findIPv6Address();
                if (ipv6Addr != null) {
                    metadata.put(IPV6, ipv6Addr);
                }
            }
            else if (IPV4.equalsIgnoreCase(ipType)) {
                ip = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
            }
            else if (IPV6.equalsIgnoreCase(ipType)) {
                ip = inetIPv6Utils.findIPv6Address();
                if (StringUtils.isEmpty(ip)) {
                    log.warn("There is no available IPv6 found. Spring Cloud Alibaba will automatically find IPv4.");
                    ip = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
                }
            }
            else {
                throw new IllegalArgumentException(
                    "please checking the type of IP " + ipType);
            }
        }
        else {
            NetworkInterface netInterface = null;
            try {
                netInterface = NetworkInterface.getByName(networkInterface);
            } catch (SocketException ex) {
                log.error("Get network interface error, name: {}", networkInterface, ex);
            }
            if (null == netInterface) {
                throw new IllegalArgumentException(
                    "no such interface " + networkInterface);
            }

            String ipv4 = null;
            String ipv6 = null;
            Enumeration<InetAddress> inetAddress = netInterface.getInetAddresses();
            while (inetAddress.hasMoreElements()) {
                InetAddress currentAddress = inetAddress.nextElement();
                if (currentAddress instanceof Inet4Address
                    || currentAddress instanceof Inet6Address
                    && !currentAddress.isLoopbackAddress()) {
                    if (ipv4 == null && currentAddress instanceof Inet4Address addr) {
                        ipv4 = addr.getHostAddress();
                    }
                    if (ipv6 == null && currentAddress instanceof Inet6Address addr) {
                        ipv6 = addr.getHostAddress();
                    }
                }
            }

            if (IPV4.equalsIgnoreCase(ipType)) {
                ip = ipv4;
                if (StringUtils.isEmpty(ip)) {
                    ip = properties.getDefaultIpAddress();
                }
            } else if (IPV6.equalsIgnoreCase(ipType)) {
                ip = ipv6;
                if (StringUtils.isEmpty(ip)) {
                    log.warn("There is no available IPv6 found in " + networkInterface + ". Spring Cloud Alibaba will automatically find IPv4.");
                    ip = ipv4;
                    if (StringUtils.isEmpty(ip)) {
                        ip = properties.getDefaultIpAddress();
                    }
                }
            } else {
                if (!StringUtils.isEmpty(ipv4)) {
                    ip = ipv4;
                } else if (!StringUtils.isEmpty(ipv6)) {
                    ip = ipv6;
                } else {
                    ip = properties.getDefaultIpAddress();
                }
            }

            if (StringUtils.isEmpty(ip)) {
                throw new IllegalStateException("cannot find available ip from"
                    + " network interface " + networkInterface);
            }

        }

        return ip;
    }

}
