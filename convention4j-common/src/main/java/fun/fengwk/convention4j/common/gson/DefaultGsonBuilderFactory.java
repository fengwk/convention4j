package fun.fengwk.convention4j.common.gson;

import com.google.gson.GsonBuilder;
import fun.fengwk.convention4j.common.ClassUtils;
import fun.fengwk.convention4j.common.OrderedObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

/**
 * 
 * @author fengwk
 */
public class DefaultGsonBuilderFactory {

    private static final List<GsonBuilderConfigurator> CONFIGURATORS;

    static {
        List<GsonBuilderConfigurator> configurators = new ArrayList<>();
        ServiceLoader<GsonBuilderConfigurator> sl = ServiceLoader.load(
            GsonBuilderConfigurator.class, ClassUtils.getDefaultClassLoader());
        Iterator<GsonBuilderConfigurator> iterator = sl.iterator();
        while (iterator.hasNext()) {
            GsonBuilderConfigurator configurator;
            try {
                configurator = iterator.next();
            } catch (ServiceConfigurationError err) {
                if (err.getCause() != null
                    // ignore for JsonPathGsonBuilderConfigurator
                    && Objects.equals("com/jayway/jsonpath/Configuration$Defaults", err.getCause().getMessage())) {
                    continue;
                }
                throw err;
            }
            configurator.init();
            configurators.add(configurator);
        }
        CONFIGURATORS = OrderedObject.sort(configurators);
    }

    private DefaultGsonBuilderFactory() {}
    
    public static GsonBuilder builder() {
        GsonBuilder builder = new GsonBuilder();
        for (GsonBuilderConfigurator configurator : CONFIGURATORS) {
            configurator.config(builder);
        }
        return builder;
    }
    
}
