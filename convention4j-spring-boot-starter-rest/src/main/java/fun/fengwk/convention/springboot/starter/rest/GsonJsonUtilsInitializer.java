package fun.fengwk.convention.springboot.starter.rest;

import fun.fengwk.convention.api.gson.GlobalGson;
import fun.fengwk.jwt4j.JWT;
import fun.fengwk.jwt4j.JsonUtilsInitializer;
import fun.fengwk.jwt4j.support.GsonJsonUtilsAdapter;

/**
 * @author fengwk
 */
public class GsonJsonUtilsInitializer implements JsonUtilsInitializer {

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void init() {
        JWT.registerJsonUtils(new GsonJsonUtilsAdapter(GlobalGson.getInstance()));
    }

}
