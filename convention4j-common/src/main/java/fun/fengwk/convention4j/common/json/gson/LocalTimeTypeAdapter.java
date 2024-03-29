package fun.fengwk.convention4j.common.json.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalTime;

/**
 * 
 * @author fengwk
 */
public class LocalTimeTypeAdapter implements GsonTypeAdapter<LocalTime> {

    @Override
    public JsonElement serialize(LocalTime src, Type typeOfSrc, JsonSerializationContext context) {
        return src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.toString());
    }

    @Override
    public LocalTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return json == null || json.isJsonNull() ? null : LocalTime.parse(json.getAsString());
    }
    
}
