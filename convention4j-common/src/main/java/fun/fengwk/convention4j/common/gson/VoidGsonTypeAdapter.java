package fun.fengwk.convention4j.common.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

import java.lang.reflect.Type;

/**
 * for jdk17
 * @author fengwk
 */
public class VoidGsonTypeAdapter implements GsonTypeAdapter<Void> {

    @Override
    public Void deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return context.deserialize(json, String.class);
    }

    @Override
    public JsonElement serialize(Void src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(null, String.class);
    }

}
