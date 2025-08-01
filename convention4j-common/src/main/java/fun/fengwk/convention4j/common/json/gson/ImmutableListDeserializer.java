package fun.fengwk.convention4j.common.json.gson;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.GsonTypes;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;


/**
 *
 * @author fengwk
 */
public class ImmutableListDeserializer implements JsonDeserializer<ImmutableList<?>> {

    @Override
    public ImmutableList<?> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        // 先以List类型方式处理
        List<?> list = jsonDeserializationContext.deserialize(jsonElement, toListType(type));
        // 再复制为ImmutableList
        return ImmutableList.copyOf(list);
    }

    private Type toListType(Type immutableListType) {
        if (immutableListType instanceof ParameterizedType) {
            ParameterizedType immutableListPt = (ParameterizedType) immutableListType;
            return GsonTypes.newParameterizedTypeWithOwner(null, List.class,
                    immutableListPt.getActualTypeArguments());
        } else {
            return List.class;
        }
    }

}
