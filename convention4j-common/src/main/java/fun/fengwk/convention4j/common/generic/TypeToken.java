package fun.fengwk.convention4j.common.generic;

import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * TypeToken可以方便地获取一个Type，示例：
 * {@code new TypeToken<Map<String, String>>() {}.getType()}
 * 
 * @author fengwk
 */
public abstract class TypeToken<T> {
    
    private static final ConcurrentMap<Type, Type> TYPE_CACHE = new ConcurrentHashMap<>();
    
    public Type getType() {
        Type type = new TypeResolver(getClass())
                .as(TypeToken.class)
                .asParameterizedType()
                .getActualTypeArguments()[0];
        
        // 使用缓存是为了兼容某些框架，例如FastJson由于使用IdentityMap因此并非使用equals确认Type的唯一性
        return TYPE_CACHE.computeIfAbsent(type, Function.identity());
    }

}
