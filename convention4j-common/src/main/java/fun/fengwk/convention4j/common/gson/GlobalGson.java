package fun.fengwk.convention4j.common.gson;

import com.google.gson.Gson;

/**
 * @author fengwk
 */
public class GlobalGson {

    private static final Gson INSTANCE = DefaultGsonBuilderFactory.builder().create();
    
    private GlobalGson() {}

    public static Gson getInstance() {
        return INSTANCE;
    }

}
