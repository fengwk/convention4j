package fun.fengwk.convention4j.common.json.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.sql.Date;

/**
 * 
 * @author fengwk
 */
public class SqlDateTypeAdapter implements GsonTypeAdapter<Date> {

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        return src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.getTime());
    }
    
    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return json == null || json.isJsonNull() ? null : new Date(json.getAsLong());
    }
    
}
