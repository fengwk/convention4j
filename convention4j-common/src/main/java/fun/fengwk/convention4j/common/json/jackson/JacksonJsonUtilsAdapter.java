package fun.fengwk.convention4j.common.json.jackson;

import fun.fengwk.convention4j.common.json.JsonUtilsAdapter;

import java.lang.reflect.Type;

/**
 * @author fengwk
 */
public class JacksonJsonUtilsAdapter implements JsonUtilsAdapter {

    private static final JacksonJsonUtilsAdapter INSTANCE = new JacksonJsonUtilsAdapter();

    public static JacksonJsonUtilsAdapter getInstance() {
        return INSTANCE;
    }

    @Override
    public String toJson(Object obj) {
        return JacksonUtils.writeValueAsString(obj);
    }

    @Override
    public <T> T fromJson(String json, Class<T> classOfT) {
        return JacksonUtils.readValue(json, classOfT);
    }

    @Override
    public <T> T fromJson(String json, Type typeOfT) {
        return JacksonUtils.readValue(json, new TypeReferenceWrapper<>(typeOfT));
    }

}
