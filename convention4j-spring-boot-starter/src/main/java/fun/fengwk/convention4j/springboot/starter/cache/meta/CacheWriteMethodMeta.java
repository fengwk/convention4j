package fun.fengwk.convention4j.springboot.starter.cache.meta;

import fun.fengwk.convention4j.springboot.starter.cache.exception.CacheParseException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public class CacheWriteMethodMeta extends CacheMethodMeta {

    public CacheWriteMethodMeta(
        Method method, CacheConfigMeta cacheConfig, List<MethodKeyMeta> cacheKeyMetas) {
        super(method, cacheConfig, cacheKeyMetas);
        if (cacheKeyMetas.isEmpty()) {
            throw new CacheParseException(
                "Cache write method '" + method + "' must have at least one @IdKey");
        }
        boolean multi = cacheKeyMetas.get(0).isMulti();
        for (int i = 1; i < cacheKeyMetas.size(); i++) {
            if (cacheKeyMetas.get(i).isMulti() != multi) {
                throw new CacheParseException(
                    "Multi property and non-multi property cannot be mixed: " + method);
            }
        }
    }

    public Map<String, Object> buildIdKeyMapByParameters(Object...args) {
        return KeyMeta.buildKeyMap(cacheKeyMetas.stream().filter(KeyMeta::isId).collect(Collectors.toList()),
            k -> k.selectParameter(args), KeyMeta::getValue);
    }

    public List<Map<String, Object>> buildIdKeyMapListByParameters(Object...args) {
        return KeyMeta.buildKeyMapList(cacheKeyMetas.stream().filter(KeyMeta::isId).collect(Collectors.toList()),
            k -> k.selectParameter(args), KeyMeta::getValue);
    }

}
