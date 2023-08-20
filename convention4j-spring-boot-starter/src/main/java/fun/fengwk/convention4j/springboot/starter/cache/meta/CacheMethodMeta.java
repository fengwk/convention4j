package fun.fengwk.convention4j.springboot.starter.cache.meta;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
@Data
public abstract class CacheMethodMeta {

    protected final Method method;
    protected final CacheConfigMeta cacheConfig;
    protected final List<MethodKeyMeta> cacheKeyMetas;

    public CacheMethodMeta(
        Method method, CacheConfigMeta cacheConfig, List<MethodKeyMeta> cacheKeyMetas) {
        cacheKeyMetas = cacheKeyMetas.stream()
            .sorted(Comparator.comparing(MethodKeyMeta::getParameterIndex)
                .thenComparing(MethodKeyMeta::getName))
            .collect(Collectors.toList());

        this.method = method;
        this.cacheConfig = cacheConfig;
        this.cacheKeyMetas = cacheKeyMetas;
    }

    public boolean isMulti() {
        return !cacheKeyMetas.isEmpty() && cacheKeyMetas.get(0).isMulti();
    }

    public Map<String, Object> buildKeyMapByParameters(Object...args) {
        return KeyMeta.buildKeyMap(cacheKeyMetas, k -> k.selectParameter(args), KeyMeta::getValue);
    }

    public List<Map<String, Object>> buildKeyMapListByParameters(Object...args) {
        return KeyMeta.buildKeyMapList(cacheKeyMetas, k -> k.selectParameter(args), KeyMeta::getValue);
    }

}
