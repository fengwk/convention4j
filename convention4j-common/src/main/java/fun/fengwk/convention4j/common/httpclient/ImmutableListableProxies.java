package fun.fengwk.convention4j.common.httpclient;

import java.net.Proxy;
import java.util.List;

/**
 * @author fengwk
 */
public class ImmutableListableProxies implements ListableProxies {

    private final List<Proxy> proxies;

    public ImmutableListableProxies(List<Proxy> proxies) {
        this.proxies = List.copyOf(proxies);
    }

    @Override
    public List<Proxy> listProxies() {
        return proxies;
    }

}
