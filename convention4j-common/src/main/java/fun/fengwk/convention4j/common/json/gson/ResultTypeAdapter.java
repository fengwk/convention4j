package fun.fengwk.convention4j.common.json.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.internal.GsonTypes;
import fun.fengwk.convention4j.api.result.DefaultResult;
import fun.fengwk.convention4j.api.result.Result;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 
 * @author fengwk
 */
public class ResultTypeAdapter implements GsonTypeAdapter<Result<?>> {

    @Override
    public JsonElement serialize(Result<?> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonElement jsonElement = context.serialize(src, DefaultResult.class);
//        if (jsonElement != null && jsonElement.isJsonObject()) {
//            jsonElement.getAsJsonObject().remove("errorCode");
//        }
        return jsonElement;
    }

    @Override
    public Result<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return context.deserialize(json, toCursorPageBeanType(typeOfT));
    }

    private Type toCursorPageBeanType(Type pageType) {
        if (pageType instanceof ParameterizedType) {
            ParameterizedType pagePt = (ParameterizedType) pageType;
            return GsonTypes.newParameterizedTypeWithOwner(null, DefaultResult.class,
                pagePt.getActualTypeArguments());
        } else {
            return DefaultResult.class;
        }
    }

}
