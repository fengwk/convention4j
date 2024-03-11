package fun.fengwk.convention4j.common.json.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import fun.fengwk.convention4j.common.util.LazyServiceLoader;

import java.util.List;

/**
 * @author fengwk
 */
public class ObjectMapperFactory {

    private static final List<ObjectMapperConfigurator> CONFIGURATORS
        = LazyServiceLoader.loadServiceIgnoreLoadFailed(ObjectMapperConfigurator.class);

    public static ObjectMapper create() {
        ObjectMapper objectMapper = new ObjectMapper();
        for (ObjectMapperConfigurator configurator : CONFIGURATORS) {
            configurator.configure(objectMapper);
        }
        return objectMapper;
    }

}
