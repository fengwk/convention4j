package fun.fengwk.convention4j.springboot.starter.cache.registry;

import fun.fengwk.convention4j.springboot.starter.cache.annotation.meta.ListenKeyMeta;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ResolvableType;

import java.util.List;
import java.util.function.Function;

/**
 * @author fengwk
 */
@Slf4j
@Data
public class DefaultListenKey {

    private final ListenKeyMeta listenKeyMeta;
    private final Function<Object[], Object> extractListenKeyGroupListFromParams;
    private final PropertyPath extractListenKeyGroupListFromObj;
    private final ResolvableType type;

    public boolean isMulti() {
        Class<?> clazz = type.resolve();
        if (clazz == null) {
            return false;
        }
        return Iterable.class.isAssignableFrom(clazz) || clazz.isArray();
    }

    public List<?> adaptList(Object obj) {
        return CacheInitializationUtils.adaptList(obj);
    }

}
