package fun.fengwk.convention4j.common.json.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author fengwk
 */
public class ObjectMapperFactory {

    private static final List<ObjectMapperConfigurator> CONFIGURATORS;

    static {
        List<ObjectMapperConfigurator> configurators = new ArrayList<>();
        ServiceLoader<ObjectMapperConfigurator> sl = ServiceLoader.load(ObjectMapperConfigurator.class);
        Iterator<ObjectMapperConfigurator> it = sl.iterator();
        while (it.hasNext()) {
            ObjectMapperConfigurator configurator = it.next();
            configurators.add(configurator);
        }
        CONFIGURATORS = configurators;
    }

    public static ObjectMapper create() {
        ObjectMapper objectMapper = new ObjectMapper();
        for (ObjectMapperConfigurator configurator : CONFIGURATORS) {
            configurator.configure(objectMapper);
        }
        return objectMapper;
    }

}
