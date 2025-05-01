package fun.fengwk.convention4j.common.http.client;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 可配置的代理选择器
 *
 * @author fengwk
 */
@Slf4j
public class ProxySelectorAdapter extends ProxySelector {

    private final ListableProxies listableProxies;

    public ProxySelectorAdapter(ListableProxies listableProxies) {
        this.listableProxies = Objects.requireNonNull(listableProxies);
    }

    @Override
    public List<Proxy> select(URI uri) {
        List<Proxy> proxies = listableProxies.listProxies();
        if (!proxies.isEmpty()) {
            String scheme = uri.getScheme();
            if ("http".equals(scheme) || "https".equals(scheme) || "ftp".equals(scheme) || "ftps".equals(scheme)) {
                for (Proxy proxy : proxies) {
                    if (proxy.type() == Proxy.Type.HTTP) {
                        return Collections.singletonList(proxy);
                    }
                }
            } else {
                for (Proxy proxy : proxies) {
                    if (proxy.type() == Proxy.Type.SOCKS) {
                        return Collections.singletonList(proxy);
                    }
                }
            }
        }
        return Collections.singletonList(Proxy.NO_PROXY);
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        log.error("Failed to connect to '{}'", uri, ioe);
    }

}
