package fun.fengwk.convention4j.common.json.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author fengwk
 */
public interface ObjectMapperConfigurator {

    /**
     * 配置ObjectMapper
     *
     * @param objectMapper objectMapper
     */
    void configure(ObjectMapper objectMapper);

}
