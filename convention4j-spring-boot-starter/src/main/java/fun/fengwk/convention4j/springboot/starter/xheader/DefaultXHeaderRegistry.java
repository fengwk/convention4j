package fun.fengwk.convention4j.springboot.starter.xheader;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fengwk
 */
public class DefaultXHeaderRegistry implements XHeaderRegistry {

    private final Set<String> xHeaderNames = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public void addXHeaderName(String name) {
        xHeaderNames.add(name);
    }

    @Override
    public Set<String> getXHeaderNames() {
        return new HashSet<>(xHeaderNames);
    }

}
