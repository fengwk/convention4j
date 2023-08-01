package fun.fengwk.convention4j.common.gson;

import com.google.gson.Gson;

/**
 * @author fengwk
 */
public class GsonHolder {

    private static volatile Gson instance = DefaultGsonBuilderFactory.builder().create();
    
    private GsonHolder() {}

    public static Gson getInstance() {
        return instance;
    }

    public static void setInstance(Gson instance) {
        GsonHolder.instance = instance;
    }

}
