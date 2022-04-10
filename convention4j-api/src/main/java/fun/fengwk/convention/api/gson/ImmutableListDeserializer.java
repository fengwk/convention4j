package fun.fengwk.convention.api.gson;

import com.google.common.collect.ImmutableList;
import com.google.gson.*;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

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
            return ParameterizedTypeImpl.make(List.class, immutableListPt.getActualTypeArguments(),
                    immutableListPt.getOwnerType());
        } else {
            return List.class;
        }
    }

}
