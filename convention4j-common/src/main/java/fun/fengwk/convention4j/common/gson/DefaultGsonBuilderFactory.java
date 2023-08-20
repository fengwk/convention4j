package fun.fengwk.convention4j.common.gson;

import com.google.gson.GsonBuilder;
import fun.fengwk.convention4j.common.ClassUtils;
import fun.fengwk.convention4j.common.OrderedObject;

import java.util.*;

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
                configurator.init();
            } catch (ServiceConfigurationError | NoClassDefFoundError ex) {
                NoClassDefFoundError ncEx = null;
                if (ex instanceof NoClassDefFoundError) {
                    ncEx = (NoClassDefFoundError) ex;
                }
                if (ex.getCause() instanceof NoClassDefFoundError) {
                    ncEx = (NoClassDefFoundError) ex.getCause();
                }
                if (ncEx != null
                    // ignore for JsonPathGsonBuilderConfigurator
                    && Objects.equals("com/jayway/jsonpath/Configuration$Defaults", ncEx.getMessage())) {
                    continue;
                }
                throw ex;
            }
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
