package fun.fengwk.convention4j.springboot.starter.cache.adapter;

import fun.fengwk.convention4j.common.cache.facade.CacheFacade;
import fun.fengwk.convention4j.springboot.starter.TestApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author fengwk
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class CacheAdapterTest {

    @Autowired
    private CacheFacade cacheFacade;

    @Test
    public void test() {
        cacheFacade.set("123", "456", 100);
        assert Objects.equals("456", cacheFacade.get("123"));

        Map<String, String> kvMap = new HashMap<>();
        kvMap.put("k1", "v1");
        kvMap.put("k2", "v2");
        kvMap.put("k3", "v3");
        cacheFacade.batchSet(kvMap, 100);
        assert Objects.equals("v1", cacheFacade.get("k1"));
        assert Objects.equals("v2", cacheFacade.get("k2"));
        assert Objects.equals("v3", cacheFacade.get("k3"));
        Map<String, String> foundKvMap = cacheFacade.batchGet(Arrays.asList("k1", "k2", "k3"));
        assert Objects.equals("v1", foundKvMap.get("k1"));
        assert Objects.equals("v2", foundKvMap.get("k2"));
        assert Objects.equals("v3", foundKvMap.get("k3"));

        cacheFacade.batchDelete(Arrays.asList("k1", "k2"));
        assert !Objects.equals("v1", cacheFacade.get("k1"));
        assert !Objects.equals("v2", cacheFacade.get("k2"));
        assert Objects.equals("v3", cacheFacade.get("k3"));
    }

}
