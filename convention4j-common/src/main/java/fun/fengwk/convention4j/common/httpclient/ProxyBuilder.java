package fun.fengwk.convention4j.common.httpclient;

import lombok.Data;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

/**
 * 代理配置
 *
 * @author fengwk
 */
@Data
public class ProxyBuilder {

    /**
     * 主机名
     */
    private String hostname;

    /**
     * 端口号
     */
    private int port;

    /**
     * 代理类型
     */
    private Type type;

    public Proxy newProxy() {
        InetSocketAddress address = new InetSocketAddress(hostname, port);
        return new Proxy(type, address);
    }

}
