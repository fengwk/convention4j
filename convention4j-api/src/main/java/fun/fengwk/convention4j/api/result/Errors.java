package fun.fengwk.convention4j.api.result;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author fengwk
 */
public class Errors extends LinkedHashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    private static final String CODE = "code";

    public String getCode() {
        return (String) get(CODE);
    }

    public void setCode(String code) {
        put(CODE, code);
    }

    public Map<String, Object> withoutCode() {
        Map<String, Object> map = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : entrySet()) {
            if (!CODE.equals(entry.getKey())) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

}
