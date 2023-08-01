package fun.fengwk.convention4j.common.gson;

import com.google.auto.service.AutoService;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

import java.util.EnumSet;
import java.util.Set;

/**
 * @author fengwk
 */
@AutoService(GsonBuilderConfigurator.class)
public class JsonPathGsonBuilderConfigurator implements GsonBuilderConfigurator {

    @Override
    public void init() {
        Configuration.setDefaults(new Configuration.Defaults() {

            private final JsonProvider jsonProvider = new GsonJsonProvider(GsonHolder.getInstance());
            private final MappingProvider mappingProvider = new GsonMappingProvider(GsonHolder.getInstance());

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                return EnumSet.noneOf(Option.class);
            }

        });
    }

    @Override
    public void config(GsonBuilder builder) {
        // nothing to do
    }

}
