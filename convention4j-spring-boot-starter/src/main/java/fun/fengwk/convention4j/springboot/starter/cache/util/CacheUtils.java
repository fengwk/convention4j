package fun.fengwk.convention4j.springboot.starter.cache.util;

import fun.fengwk.convention4j.common.StringUtils;
import org.springframework.util.DigestUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
public class CacheUtils {

    private static final String SEPARATOR = "@";
    private static final String PREFIX_LV1_CACHE_KEY = CacheUtils.class.getName() + "@PREFIX_LV1_CACHE_KEY";
    private static final String PREFIX_LV2_CACHE_KEY = CacheUtils.class.getName() + "@PREFIX_LV2_CACHE_KEY";
    private static final String PREFIX_ID_CACHE_KEY = CacheUtils.class.getName() + "@PREFIX_ID_CACHE_KEY";

    public static String buildLv1CacheKey(String version, Method method, Map<String, Object> keyMap) {
        List<Object> keyValues = keyMap.entrySet().stream().sorted(Map.Entry.comparingByKey())
            .map(Map.Entry::getValue).collect(Collectors.toList());
        String clearKey = buildKey(version, PREFIX_LV1_CACHE_KEY, method.toString(), keyValues);
        return DigestUtils.md5DigestAsHex(clearKey.getBytes(StandardCharsets.UTF_8));
    }

    public static String buildLv2CacheKey(String version, Collection<String> uuids, Object[] args) {
        uuids = uuids.stream().sorted(String::compareTo).collect(Collectors.toList());
        String clearKey = buildKey(version, PREFIX_LV2_CACHE_KEY, uuids, args);
        return DigestUtils.md5DigestAsHex(clearKey.getBytes(StandardCharsets.UTF_8));
    }

    public static String buildIdCacheKey(String version, String idStr) {
        String clearKey = buildKey(version, PREFIX_ID_CACHE_KEY, idStr);
        return DigestUtils.md5DigestAsHex(clearKey.getBytes(StandardCharsets.UTF_8));
    }

    private static String buildKey(String prefix, Object... args) {
        return prefix + doBuildKey(args);
    }

    private static String doBuildKey(Object... args) {
        if (args == null || args.length == 0) {
            return StringUtils.EMPTY;
        }

        StringBuilder sb = new StringBuilder();
        for (Object arg : args) {
            if (arg == null) {
                sb.append(SEPARATOR).append("null");
            } else if (arg instanceof CacheKeyable) {
                sb.append(SEPARATOR).append(((CacheKeyable) arg).toKey());
            } else if (arg instanceof Iterable) {
                Iterable<?> it = (Iterable<?>) arg;
                for (Object obj : it) {
                    sb.append(SEPARATOR).append(doBuildKey(obj));
                }
            } else if (arg.getClass().isArray()) {
                int len = Array.getLength(arg);
                for (int i = 0; i < len; i++) {
                    sb.append(SEPARATOR).append(doBuildKey(Array.get(arg, i)));
                }
            } else {
                sb.append(SEPARATOR).append(arg);
            }
        }
        return sb.toString();
    }

    public static <T> List<T> adaptList(Object obj) {
        if (obj == null) {
            return Collections.emptyList();
        } else if (obj.getClass().isArray()) {
            List<T> list = new ArrayList<>();
            int len = Array.getLength(obj);
            for (int i = 0; i < len; i++) {
                list.add((T) Array.get(obj, i));
            }
            return list;
        } else if (obj instanceof Iterable) {
            Iterable<?> it = (Iterable<?>) obj;
            List<T> list = new ArrayList<>();
            for (Object o : it) {
                list.add((T) o);
            }
            return list;
        } else {
            return Collections.singletonList((T) obj);
        }
    }

}
