package fun.fengwk.convention4j.common.cache.key;

import fun.fengwk.convention4j.common.json.JsonUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author fengwk
 */
public class KeyUtils {

    private KeyUtils() {}

    public static TreeMap<String, Object> adaptKeyable(TreeMap<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return map;
        }
        TreeMap<String, Object> res = new TreeMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            res.put(entry.getKey(), adaptKeyable(entry.getValue()));
        }
        return res;
    }

    public static Object[] adaptKeyable(Object[] objs) {
        if (objs == null || objs.length == 0) {
            return objs;
        }
        Object[] res = new Object[objs.length];
        for (int i = 0; i < objs.length; i++) {
            objs[i] = adaptKeyable(objs[i]);
        }
        return res;
    }

    public static String toZipKey(ZipKey zipKey) {
        if (zipKey == null) {
            return null;
        }
        String json = JsonUtils.toJson(zipKey);
        return DigestUtils.md5DigestAsHex(json.getBytes(StandardCharsets.UTF_8));
    }

    public static Set<String> toZipKeySet(Collection<? extends ZipKey> zipKeys) {
        if (zipKeys == null || zipKeys.isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> set = new HashSet<>();
        for (ZipKey zipKey : zipKeys) {
            String zk = toZipKey(zipKey);
            set.add(zk);
        }
        return set;
    }

    private static Object adaptKeyable(Object obj) {
        if (obj instanceof Keyable) {
            return ((Keyable) obj).toKey();
        } else {
            return obj;
        }
    }

}
