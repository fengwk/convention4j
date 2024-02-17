package fun.fengwk.convention4j.common.json.gson;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

/**
 * 
 * @author fengwk
 */
public interface GsonTypeAdapter<T> extends JsonSerializer<T>, JsonDeserializer<T> {

}
