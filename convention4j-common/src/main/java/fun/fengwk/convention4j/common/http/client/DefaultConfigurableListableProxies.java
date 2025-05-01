package fun.fengwk.convention4j.common.http.client;

import java.net.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author fengwk
 */
public class DefaultConfigurableListableProxies implements ConfigurableListableProxies {

    private final CopyOnWriteArrayList<Proxy> proxies = new CopyOnWriteArrayList<>();

    @Override
    public void addProxy(Proxy proxy) {
        proxies.add(proxy);
    }

    @Override
    public void removeProxy(Proxy proxy) {
        proxies.remove(proxy);
    }

    @Override
    public List<Proxy> listProxies() {
        return Collections.unmodifiableList(proxies);
    }

}
