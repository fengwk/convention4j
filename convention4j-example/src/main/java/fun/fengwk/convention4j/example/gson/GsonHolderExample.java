package fun.fengwk.convention4j.example.gson;

import com.google.gson.Gson;
import fun.fengwk.convention4j.common.MapUtils;
import fun.fengwk.convention4j.common.gson.GsonHolder;

/**
 * @author fengwk
 */
public class GsonHolderExample {

    public static void main(String[] args) {
        Gson gson = GsonHolder.getInstance();
        String json = gson.toJson(MapUtils.newMap("name", "fengwk"));
        System.out.println(json);
    }

}
