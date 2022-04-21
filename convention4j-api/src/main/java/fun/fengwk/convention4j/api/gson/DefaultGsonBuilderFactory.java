package fun.fengwk.convention4j.api.gson;

import com.google.gson.GsonBuilder;
import fun.fengwk.convention4j.common.OrderedObject;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * 
 * @author fengwk
 */
public class DefaultGsonBuilderFactory {

    private static final List<GsonBuilderConfigurator> CONFIGURATORS;

    static {
        List<GsonBuilderConfigurator> configurators = new ArrayList<>();
        ServiceLoader<GsonBuilderConfigurator> sl = ServiceLoader.load(GsonBuilderConfigurator.class);
        for (GsonBuilderConfigurator configurator : sl) {
            configurators.add(configurator);
        }
        CONFIGURATORS = OrderedObject.sort(configurators);
    }

    private DefaultGsonBuilderFactory() {}
    
    public static GsonBuilder create() {
        GsonBuilder builder = new GsonBuilder();
        for (GsonBuilderConfigurator configurator : CONFIGURATORS) {
            configurator.config(builder);
        }
        return builder;
    }
    
}
