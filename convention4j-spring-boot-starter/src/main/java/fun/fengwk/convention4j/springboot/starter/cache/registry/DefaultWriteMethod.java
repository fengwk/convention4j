package fun.fengwk.convention4j.springboot.starter.cache.registry;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;

/**
 * @author fengwk
 */
@Data
public class DefaultWriteMethod<O> {

    private final Method writeMethod;
    private final Function<Object[], List<O>> objQueryFunc;

}
