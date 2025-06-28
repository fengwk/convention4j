package fun.fengwk.convention4j.springboot.starter.transport;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fengwk
 */
public class DefaultTransportHeaders implements TransportHeaders {

    private final Set<String> transportHeaders = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void addHeader(String headerName) {
        transportHeaders.add(headerName);
    }

    public void removeHeader(String headerName) {
        transportHeaders.remove(headerName);
    }

    public Set<String> viewHeaders() {
        return Collections.unmodifiableSet(transportHeaders);
    }

}
