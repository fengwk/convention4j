package fun.fengwk.convention.api.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.reflect.TypeToken;
import fun.fengwk.convention.api.result.Result;
import fun.fengwk.convention.api.result.ResultImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * 
 * @author fengwk
 */
public class ResultTypeAdapter implements GsonTypeAdapter<Result<?>> {

    private static final String PROPERTY_SUCCESS = "success";
    private static final String PROPERTY_CODE = "code";
    private static final String PROPERTY_MESSAGE = "message";
    private static final String PROPERTY_DATA = "data";
    private static final String PROPERTY_ERRORS = "errors";
    
    @Override
    public JsonElement serialize(Result<?> src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return JsonNull.INSTANCE;
        }
        
        JsonObject jsonObject = new JsonObject();
        
        jsonObject.addProperty(PROPERTY_SUCCESS, src.isSuccess());
        
        if (src.getCode() == null) {
            jsonObject.add(PROPERTY_CODE, JsonNull.INSTANCE);
        } else {
            jsonObject.addProperty(PROPERTY_CODE, src.getCode());
        }
        
        if (src.getMessage() == null) {
            jsonObject.add(PROPERTY_MESSAGE, JsonNull.INSTANCE);
        } else {
            jsonObject.addProperty(PROPERTY_MESSAGE, src.getMessage());
        }
        
        jsonObject.add(PROPERTY_DATA, src.getData() == null ? JsonNull.INSTANCE : context.serialize(src.getData()));
        
        jsonObject.add(PROPERTY_ERRORS, src.getErrors() == null ? JsonNull.INSTANCE : context.serialize(src.getErrors()));
        
        return jsonObject;
    }
    
    @Override
    public Result<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null || json.isJsonNull()) {
            return null;
        } else if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            boolean success = deserializeSuccess(jsonObject.get(PROPERTY_SUCCESS));
            String code = deserializeCode(jsonObject.get(PROPERTY_CODE));
            String message = deserializeMessage(jsonObject.get(PROPERTY_MESSAGE));
            Object data = deserializeData(jsonObject.get(PROPERTY_DATA), typeOfT, context);
            Map<String, String> errors = deserializeErrors(jsonObject.get(PROPERTY_ERRORS), context);
            return new ResultImpl<>(success, code, message, data, errors);
        } else {
            throw new JsonParseException(String.format("Json[%s] shoud be null or object", json));
        }
    }
    
    private boolean deserializeSuccess(JsonElement jsonEle) {
        if (jsonEle == null || jsonEle.isJsonNull()) {
            throw new JsonParseException(String.format("Result must be have property %s", PROPERTY_SUCCESS));
        }
        
        return jsonEle.getAsBoolean();
    }
    
    private String deserializeCode(JsonElement jsonEle) {
        return jsonEle == null || jsonEle.isJsonNull() ? null : jsonEle.getAsString();
    }
    
    private String deserializeMessage(JsonElement jsonEle) {
        return jsonEle == null || jsonEle.isJsonNull() ? null : jsonEle.getAsString();
    }
    
    private Object deserializeData(JsonElement jsonEle, Type type, JsonDeserializationContext context) {
        if (jsonEle == null || jsonEle.isJsonNull()) {
            return null;
        }
        
        Object data;
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            Type[] types = pType.getActualTypeArguments();
            if (pType == null || types.length != 1) {
                throw new JsonParseException(String.format("Unknown type %s", type));
            }
            data = context.deserialize(jsonEle, types[0]);
        } else if (jsonEle.isJsonArray()) {
            data = context.deserialize(jsonEle, JsonArray.class);
        } else if (jsonEle.isJsonObject()) {
            data = context.deserialize(jsonEle, JsonObject.class);
        } else {
            data = jsonEle.getAsJsonPrimitive();
        }
        
        return data;
    }
    
    private Map<String, String> deserializeErrors(JsonElement jsonEle, JsonDeserializationContext context) {
        if (jsonEle == null || jsonEle.isJsonNull()) {
            return null;
        }
        
        return context.deserialize(jsonEle, new TypeToken<Map<String, String>>() {}.getType());
    }

}
