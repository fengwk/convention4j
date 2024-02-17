package fun.fengwk.convention4j.common.json;

import java.lang.reflect.Type;

/**
 * @author fengwk
 */
public interface JsonUtilsAdapter {

    String toJson(Object obj);

    <T> T fromJson(String json, Class<T> classOfT);

    <T> T fromJson(String json, Type typeOfT);

}
