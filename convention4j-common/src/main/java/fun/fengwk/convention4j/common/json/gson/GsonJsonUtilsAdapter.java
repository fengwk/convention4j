package fun.fengwk.convention4j.common.json.gson;

import fun.fengwk.convention4j.common.json.JsonUtilsAdapter;

import java.lang.reflect.Type;

/**
 * @author fengwk
 */
public class GsonJsonUtilsAdapter implements JsonUtilsAdapter {

    private static final GsonJsonUtilsAdapter INSTANCE = new GsonJsonUtilsAdapter();

    public static GsonJsonUtilsAdapter getInstance() {
        return INSTANCE;
    }

    @Override
    public String toJson(Object obj) {
        return GsonUtils.toJson(obj);
    }

    @Override
    public <T> T fromJson(String json, Class<T> classOfT) {
        return GsonUtils.fromJson(json, classOfT);
    }

    @Override
    public <T> T fromJson(String json, Type typeOfT) {
        return GsonUtils.fromJson(json, typeOfT);
    }

}
