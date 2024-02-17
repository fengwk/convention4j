package fun.fengwk.convention4j.common.json.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author fengwk
 */
public class ObjectMapperHolder {

    private static final ObjectMapper INSTANCE = ObjectMapperFactory.create();

    private ObjectMapperHolder() {}

    public static ObjectMapper getInstance() {
        return INSTANCE;
    }

}
