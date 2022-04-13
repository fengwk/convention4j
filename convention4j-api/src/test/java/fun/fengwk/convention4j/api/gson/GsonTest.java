package fun.fengwk.convention4j.api.gson;

import com.google.common.collect.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;

import java.util.Map;

/**
 * @author fengwk
 */
public class GsonTest {

    private final Gson gson = GlobalGson.getInstance();

    @Test
    public void test1() {
        ImmutableList<String> list = ImmutableList.of("v1", "v2", "v3");
        String json = gson.toJson(list);
        Object res = gson.fromJson(json, new TypeToken<ImmutableList<String>>() {}.getType());
        assert res.equals(list);
    }

    @Test
    public void test2() {
        ImmutableSet<String> set = ImmutableSet.of("v1", "v2", "v3");
        String json = gson.toJson(set);
        assert gson.fromJson(json, new TypeToken<ImmutableSet<String>>() {}.getType()).equals(set);
        assert gson.fromJson(json, new TypeToken<ImmutableSortedSet<String>>() {}.getType()).equals(set);
    }

    @Test
    public void test3() {
        ImmutableMap<String, ?> map = ImmutableMap.of("k1", "v1", "k2",
                ImmutableMap.of("k2.k1", "k2.v1", "k2.k2", "k2.v2"));
        String json = gson.toJson(map);
        assert gson.fromJson(json, new TypeToken<ImmutableMap<String, ?>>() {}.getType()).equals(map);
        assert gson.fromJson(json, new TypeToken<ImmutableSortedMap<String, ?>>() {}.getType()).equals(map);
    }

    @Test
    public void test4() {
        ImmutableList<String> list = ImmutableList.of("v1", "v2", "v3");
        String json = gson.toJson(list);
        Object res = gson.fromJson(json, new TypeToken<ImmutableCollection<String>>() {}.getType());
        assert res.equals(list);
    }

    @Test
    public void test5() {
        String json = "{\"age\":12}";
        Map<String, ?> map = gson.fromJson(json, new TypeToken<Map<String, ?>>() {}.getType());
        System.out.println(map);
    }

}
