package fun.fengwk.convention4j.common.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.Reader;
import java.lang.reflect.Type;

/**
 * 提供一套与Gson一致的静态访问方式。
 *
 * @author fengwk
 */
public class GsonUtils {

    private GsonUtils() {
    }

    public static String toJson(JsonElement jsonElement) {
        return GsonHolder.getInstance().toJson(jsonElement);
    }

    public static void toJson(JsonElement jsonElement, Appendable writer) throws JsonIOException {
        GsonHolder.getInstance().toJson(jsonElement, writer);
    }

    public static void toJson(JsonElement jsonElement, JsonWriter writer) throws JsonIOException {
        GsonHolder.getInstance().toJson(jsonElement, writer);
    }

    public static String toJson(Object src) {
        return GsonHolder.getInstance().toJson(src);
    }

    public static void toJson(Object src, Appendable writer) throws JsonIOException {
        GsonHolder.getInstance().toJson(src, writer);
    }

    public static String toJson(Object src, Type typeOfSrc) {
        return GsonHolder.getInstance().toJson(src, typeOfSrc);
    }

    public static void toJson(Object src, Type typeOfSrc, Appendable writer) throws JsonIOException {
        GsonHolder.getInstance().toJson(src, typeOfSrc, writer);
    }

    public static JsonElement toJsonTree(Object src) {
        return GsonHolder.getInstance().toJsonTree(src);
    }

    public static JsonElement toJsonTree(Object src, Type typeOfSrc) {
        return GsonHolder.getInstance().toJsonTree(src, typeOfSrc);
    }

    public static <T> T fromJson(JsonElement json, Class<T> classOfT) throws JsonSyntaxException {
        return GsonHolder.getInstance().fromJson(json, classOfT);
    }

    public static <T> T fromJson(JsonElement json, Type typeOfT) throws JsonSyntaxException {
        return GsonHolder.getInstance().fromJson(json, typeOfT);
    }

    public static <T> T fromJson(JsonReader reader, Type typeOfT) throws JsonIOException, JsonSyntaxException {
        return GsonHolder.getInstance().fromJson(reader, typeOfT);
    }

    public static <T> T fromJson(Reader json, Class<T> classOfT) throws JsonSyntaxException, JsonIOException {
        return GsonHolder.getInstance().fromJson(json, classOfT);
    }

    public static <T> T fromJson(Reader json, Type typeOfT) throws JsonIOException, JsonSyntaxException {
        return GsonHolder.getInstance().fromJson(json, typeOfT);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        return GsonHolder.getInstance().fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Type typeOfT) throws JsonSyntaxException {
        return GsonHolder.getInstance().fromJson(json, typeOfT);
    }

}
