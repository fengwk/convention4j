package fun.fengwk.convention4j.common.rocketmq;

import org.apache.rocketmq.client.apis.ClientServiceProvider;

/**
 * @author fengwk
 */
public class ClientServiceProviderHolder {

    private ClientServiceProviderHolder() {}

    private static volatile ClientServiceProvider CSP;

    public static ClientServiceProvider get() {
        ClientServiceProvider csp = CSP;
        if (csp != null) {
            return csp;
        }
        synchronized (ClientServiceProviderHolder.class) {
            if (CSP != null) {
                return CSP;
            }
            CSP = ClientServiceProvider.loadService();
            return CSP;
        }
    }

    public static void clearCache() {
        synchronized (ClientServiceProviderHolder.class) {
            CSP = null;
        }
    }

}
