package fun.fengwk.convention4j.common.json.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author fengwk
 */
public interface ObjectMapperConfigurator {

    default void init() {};

    void config(ObjectMapper objectMapper);

}
