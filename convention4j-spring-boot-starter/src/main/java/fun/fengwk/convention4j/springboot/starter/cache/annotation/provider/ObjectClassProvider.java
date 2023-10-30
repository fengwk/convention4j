package fun.fengwk.convention4j.springboot.starter.cache.annotation.provider;

import java.lang.reflect.AnnotatedElement;

/**
 * @author fengwk
 */
public interface ObjectClassProvider {

    /**
     * 获取对象类型。
     * @param element 被注释的元素。
     * @return 对象类型。
     */
    Class<?> getObjectClass(AnnotatedElement element);

}
