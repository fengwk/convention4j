package fun.fengwk.convention4j.api.gson;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

/**
 * 
 * @author fengwk
 */
public interface GsonTypeAdapter<T> extends JsonSerializer<T>, JsonDeserializer<T> {

}
