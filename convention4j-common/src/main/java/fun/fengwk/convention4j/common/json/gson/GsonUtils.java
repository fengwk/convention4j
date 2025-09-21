package fun.fengwk.convention4j.common.json.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.Reader;
import java.lang.reflect.Type;

/**
 * 提供一套与Gson一致的静态访问方式。
 *
 * @author fengwk
 */
@Slf4j
public class GsonUtils {

    private GsonUtils() {
    }

    public static String toJson(JsonElement jsonElement) {
        try {
            return GsonHolder.getInstance().toJson(jsonElement);
        } catch (Exception ex) {
            log.error("toJson error", ex);
            return null;
        }
    }

    public static void toJson(JsonElement jsonElement, Appendable writer) throws JsonIOException {
        GsonHolder.getInstance().toJson(jsonElement, writer);
    }

    public static void toJson(JsonElement jsonElement, JsonWriter writer) throws JsonIOException {
        GsonHolder.getInstance().toJson(jsonElement, writer);
    }

    public static String toJson(Object src) {
        try {
            return GsonHolder.getInstance().toJson(src);
        } catch (Exception ex) {
            log.error("toJson error", ex);
            return null;
        }
    }

    public static void toJson(Object src, Appendable writer) throws JsonIOException {
        GsonHolder.getInstance().toJson(src, writer);
    }

    public static String toJson(Object src, Type typeOfSrc) {
        try {
            return GsonHolder.getInstance().toJson(src, typeOfSrc);
        } catch (Exception ex) {
            log.error("toJson error", ex);
            return null;
        }
    }

    public static void toJson(Object src, Type typeOfSrc, Appendable writer) throws JsonIOException {
        GsonHolder.getInstance().toJson(src, typeOfSrc, writer);
    }

    public static JsonElement toJsonTree(Object src) {
        try {
            return GsonHolder.getInstance().toJsonTree(src);
        } catch (Exception ex) {
            log.error("toJsonTree error", ex);
            return null;
        }
    }

    public static JsonElement toJsonTree(Object src, Type typeOfSrc) {
        try {
            return GsonHolder.getInstance().toJsonTree(src, typeOfSrc);
        } catch (Exception ex) {
            log.error("toJsonTree error", ex);
            return null;
        }
    }

    public static <T> T fromJson(JsonElement json, Class<T> classOfT) {
        try {
            return GsonHolder.getInstance().fromJson(json, classOfT);
        } catch (Exception ex) {
            log.error("fromJson error", ex);
            return null;
        }
    }

    public static <T> T fromJson(JsonElement json, Type typeOfT) {
        try {
            return GsonHolder.getInstance().fromJson(json, typeOfT);
        } catch (Exception ex) {
            log.error("fromJson error", ex);
            return null;
        }
    }

    public static <T> T fromJson(JsonReader reader, Type typeOfT) {
        try {
            return GsonHolder.getInstance().fromJson(reader, typeOfT);
        } catch (Exception ex) {
            log.error("fromJson error", ex);
            return null;
        }
    }

    public static <T> T fromJson(Reader json, Class<T> classOfT) {
        try {
            return GsonHolder.getInstance().fromJson(json, classOfT);
        } catch (Exception ex) {
            log.error("fromJson error", ex);
            return null;
        }
    }

    public static <T> T fromJson(Reader json, Type typeOfT) {
        try {
            return GsonHolder.getInstance().fromJson(json, typeOfT);
        } catch (Exception ex) {
            log.error("fromJson error", ex);
            return null;
        }
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
            return GsonHolder.getInstance().fromJson(json, classOfT);
        } catch (Exception ex) {
            log.error("fromJson error", ex);
            return null;
        }
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        try {
            return GsonHolder.getInstance().fromJson(json, typeOfT);
        } catch (Exception ex) {
            log.error("fromJson error", ex);
            return null;
        }
    }

}
