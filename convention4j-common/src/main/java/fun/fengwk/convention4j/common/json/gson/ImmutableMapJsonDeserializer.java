package fun.fengwk.convention4j.common.json.gson;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.GsonTypes;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.SortedMap;


/**
 *
 * @author fengwk
 */
public class ImmutableMapJsonDeserializer implements JsonDeserializer<ImmutableMap<?, ?>> {

    @Override
    public ImmutableMap<?, ?> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        // 先以Map类型方式处理
        SortedMap<?, ?> map = jsonDeserializationContext.deserialize(jsonElement, toMapType(type));
        // 再复制为ImmutableMap
        return ImmutableSortedMap.copyOf(map);
    }

    private Type toMapType(Type immutableMapType) {
        if (immutableMapType instanceof ParameterizedType) {
            ParameterizedType immutableMapPt = (ParameterizedType) immutableMapType;
            return GsonTypes.newParameterizedTypeWithOwner(null, SortedMap.class,
                    immutableMapPt.getActualTypeArguments());
        } else {
            return SortedMap.class;
        }
    }

}
