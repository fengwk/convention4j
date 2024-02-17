package fun.fengwk.convention4j.common.json;

import fun.fengwk.convention4j.common.json.jackson.JacksonJsonUtilsAdapter;
import fun.fengwk.convention4j.common.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * @author fengwk
 */
public class JsonUtils {

    private static volatile JsonUtilsAdapter ADAPTER = JacksonJsonUtilsAdapter.getInstance();
    private static boolean REGISTER = false;

    public static synchronized void register(JsonUtilsAdapter adapter) {
        ADAPTER = adapter;
        REGISTER = true;
    }

    public static synchronized boolean registered() {
        return REGISTER;
    }

    public static String toJson(Object obj) {
        return ADAPTER.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return ADAPTER.fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return ADAPTER.fromJson(json, typeOfT);
    }

    public static <T> T fromJson(String json, TypeToken<T> typeOfT) {
        return ADAPTER.fromJson(json, typeOfT.getType());
    }

}
