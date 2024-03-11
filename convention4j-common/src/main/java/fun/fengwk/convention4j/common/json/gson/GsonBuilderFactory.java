package fun.fengwk.convention4j.common.json.gson;

import com.google.gson.GsonBuilder;
import fun.fengwk.convention4j.common.util.LazyServiceLoader;

import java.util.List;

/**
 * 
 * @author fengwk
 */
public class GsonBuilderFactory {

    private static final List<GsonBuilderConfigurator> CONFIGURATORS
        = LazyServiceLoader.loadServiceIgnoreLoadFailed(GsonBuilderConfigurator.class);

    private GsonBuilderFactory() {}
    
    public static GsonBuilder builder() {
        GsonBuilder builder = new GsonBuilder();
        for (GsonBuilderConfigurator configurator : CONFIGURATORS) {
            configurator.config(builder);
        }
        return builder;
    }
    
}
