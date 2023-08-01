package fun.fengwk.convention4j.common.jwt4j;

import com.google.auto.service.AutoService;
import fun.fengwk.convention4j.common.gson.GsonHolder;
import fun.fengwk.jwt4j.JWT;
import fun.fengwk.jwt4j.JsonUtilsInitializer;
import fun.fengwk.jwt4j.support.GsonJsonUtilsAdapter;

/**
 * @author fengwk
 */
@AutoService(JsonUtilsInitializer.class)
public class GsonJsonUtilsInitializer implements JsonUtilsInitializer {

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void init() {
        JWT.registerJsonUtils(new GsonJsonUtilsAdapter(GsonHolder.getInstance()));
    }

}
