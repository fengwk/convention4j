package fun.fengwk.convention4j.common.json.gson;

import com.google.gson.Gson;

/**
 * @author fengwk
 */
public class GsonHolder {

    private static final Gson INSTANCE = GsonBuilderFactory.builder().create();
    
    private GsonHolder() {}

    public static Gson getInstance() {
        return INSTANCE;
    }

}
