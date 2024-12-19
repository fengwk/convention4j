package fun.fengwk.convention4j.common.rocketmq;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fengwk
 */
public class ProcessorContext {

    private Map<String, Object> attributes = new HashMap<>();

    public void addProperty(String key, Object value) {
        attributes.put(key, value);
    }

    public Object getProperty(String key) {
        return attributes.get(key);
    }

}
