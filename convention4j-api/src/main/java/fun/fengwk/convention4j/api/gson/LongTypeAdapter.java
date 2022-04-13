package fun.fengwk.convention4j.api.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import java.lang.reflect.Type;

/**
 * 将Long类型序列化为字符串，以解决js长数字问题。
 * 
 * @author fengwk
 */
public class LongTypeAdapter implements GsonTypeAdapter<Long> {

    @Override
    public JsonElement serialize(Long src, Type typeOfSrc, JsonSerializationContext context) {
        return src == null ? JsonNull.INSTANCE : new JsonPrimitive(String.valueOf(src));
    }

    @Override
    public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return json == null || json.isJsonNull() ? null : json.getAsLong();
    }

}
