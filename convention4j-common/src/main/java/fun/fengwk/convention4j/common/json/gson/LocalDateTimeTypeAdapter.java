package fun.fengwk.convention4j.common.json.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

/**
 * 
 * @author fengwk
 */
public class LocalDateTimeTypeAdapter implements GsonTypeAdapter<LocalDateTime> {
    
    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.toString());
    }
    
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return json == null || json.isJsonNull() ? null : LocalDateTime.parse(json.getAsString());
    }

}
