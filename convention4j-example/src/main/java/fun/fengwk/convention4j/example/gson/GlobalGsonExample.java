package fun.fengwk.convention4j.example.gson;

import com.google.gson.Gson;
import fun.fengwk.convention4j.common.MapUtils;
import fun.fengwk.convention4j.common.gson.GlobalGson;

/**
 * @author fengwk
 */
public class GlobalGsonExample {

    public static void main(String[] args) {
        Gson gson = GlobalGson.getInstance();
        String json = gson.toJson(MapUtils.newMap("name", "fengwk"));
        System.out.println(json);
    }

}
