package fun.fengwk.convention.api.gson;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.SortedSet;


/**
 *
 * @author fengwk
 */
public class ImmutableSetJsonDeserializer implements JsonDeserializer<ImmutableSet<?>> {

    @Override
    public ImmutableSet<?> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        // 先以Set类型方式处理
        SortedSet<?> set = jsonDeserializationContext.deserialize(jsonElement, toSetType(type));
        // 再复制为ImmutableSet
        return ImmutableSortedSet.copyOf(set);
    }

    private Type toSetType(Type immutableSetType) {
        if (immutableSetType instanceof ParameterizedType) {
            ParameterizedType immutableSetPt = (ParameterizedType) immutableSetType;
            return ParameterizedTypeImpl.make(SortedSet.class, immutableSetPt.getActualTypeArguments(),
                    immutableSetPt.getOwnerType());
        } else {
            return SortedSet.class;
        }
    }

}
