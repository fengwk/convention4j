package fun.fengwk.convention4j.api.result;

import java.util.LinkedHashMap;

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

}
