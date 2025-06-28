package fun.fengwk.convention4j.spring.cloud.starter.gateway.dns;

import fun.fengwk.convention4j.common.lang.StringUtils;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.resolver.dns.DefaultDnsCache;
import io.netty.resolver.dns.DnsAddressResolverGroup;
import io.netty.resolver.dns.DnsNameResolverBuilder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.gateway.config.HttpClientCustomizer;
import reactor.netty.http.client.HttpClient;

/**
 * 配置 Spring Cloud Gateway DNS 缓存尊重 JDK 规范的选项
 * sun.net.inetaddr.ttl
 * sun.net.inetaddr.negative.ttl
 *
 * @see <a href="https://github.com/spring-cloud/spring-cloud-gateway/issues/561">How can I change the time of DNS Cache</a>
 * @author fengwk
 */
@AutoConfiguration
public class SpringCloudGatewayDnsCacheAutoConfiguration implements HttpClientCustomizer {

    private static final int DEFAULT_CACHE_TTL = 30;
    private static final int DEFAULT_NEG_CACHE_TTL = 5;

    @Override
    public HttpClient customize(HttpClient httpClient) {
        Integer cacheTtl = parseInteger(System.getProperty("sun.net.inetaddr.ttl"));
        Integer negCacheTtl = parseInteger(System.getProperty("sun.net.inetaddr.negative.ttl"));

        if (cacheTtl == null) {
            cacheTtl = DEFAULT_CACHE_TTL;
        }
        if (negCacheTtl == null) {
            negCacheTtl = DEFAULT_NEG_CACHE_TTL;
        }

        DnsNameResolverBuilder dnsResolverBuilder = new DnsNameResolverBuilder()
                .datagramChannelFactory(EpollDatagramChannel::new)
                .resolveCache(new DefaultDnsCache(cacheTtl, cacheTtl, negCacheTtl));
        return httpClient.resolver(new DnsAddressResolverGroup(dnsResolverBuilder));
    }

    private Integer parseInteger(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        try {
            return Integer.parseInt(s);
        } catch (Exception ignore) {
            return null;
        }
    }

}

