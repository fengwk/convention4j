package fun.fengwk.convention4j.common.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.$Gson$Types;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.api.result.DefaultResult;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 
 * @author fengwk
 */
public class ResultDeserializer implements JsonDeserializer<Result<?>> {

    @Override
    public Result<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return context.deserialize(json, toCursorPageBeanType(typeOfT));
    }

    private Type toCursorPageBeanType(Type pageType) {
        if (pageType instanceof ParameterizedType) {
            ParameterizedType pagePt = (ParameterizedType) pageType;
            return $Gson$Types.newParameterizedTypeWithOwner(null, DefaultResult.class,
                pagePt.getActualTypeArguments());
        } else {
            return DefaultResult.class;
        }
    }

}
