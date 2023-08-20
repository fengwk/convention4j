package fun.fengwk.convention4j.common.gson;

import com.google.auto.service.AutoService;
import com.google.common.collect.*;
import com.google.gson.GsonBuilder;
import fun.fengwk.convention4j.common.OrderedObject;

/**
 * @author fengwk
 */
@AutoService(GsonBuilderConfigurator.class)
public class GuavaGsonBuilderConfigurator implements GsonBuilderConfigurator {

    private static final String IMMUTABLE_COLLECTION_CLASS_NAME = "com.google.common.collect.ImmutableCollection";

    @Override
    public int getOrder() {
        return OrderedObject.HIGHEST_PRECEDENCE + 1;
    }

    @Override
    public void config(GsonBuilder builder) {
        if (isPresentGuavaDependency()) {
            builder.registerTypeAdapter(ImmutableCollection.class, new ImmutableListDeserializer());
            builder.registerTypeAdapter(ImmutableList.class, new ImmutableListDeserializer());
            builder.registerTypeAdapter(ImmutableSet.class, new ImmutableSetJsonDeserializer());
            builder.registerTypeAdapter(ImmutableSortedSet.class, new ImmutableSetJsonDeserializer());
            builder.registerTypeAdapter(ImmutableMap.class, new ImmutableMapJsonDeserializer());
            builder.registerTypeAdapter(ImmutableSortedMap.class, new ImmutableMapJsonDeserializer());
        }
    }

    /**
     * 检查是否存在guava依赖。
     *
     * @return
     */
    private boolean isPresentGuavaDependency() {
        try {
            Class.forName(IMMUTABLE_COLLECTION_CLASS_NAME);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
