package fun.fengwk.convention4j.common;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link OrderedObject}用于标识对象的有优先级。
 * 
 * @author fengwk
 */
public interface OrderedObject {

    /**
     * 最高优先级。
     */
    int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

    /**
     * 最低优先级。
     */
    int LOWEST_PRECEDENCE = Integer.MAX_VALUE;
    
    /**
     * 默认优先级。
     */
    int DEFAULT_PRECEDENCE = 5;
    
    /**
     * 获取当前对象优先级，返回值越小优先级越高。
     * 
     * @return
     */
    default int getOrder() {
        return DEFAULT_PRECEDENCE;
    }
    
    /**
     * 将指定集合按{@link OrderedObject}非降序排序，优先级越高在返回列表里的索引越小。
     * 
     * @param <E>
     * @param collection not null
     * @return
     */
    static <E extends OrderedObject> List<E> sort(Collection<E> collection) {
        return collection.stream()
                .sorted(Comparator.comparing(OrderedObject::getOrder))
                .collect(Collectors.toList());
    }
    
}
