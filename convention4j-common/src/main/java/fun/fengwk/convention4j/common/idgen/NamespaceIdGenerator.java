package fun.fengwk.convention4j.common.idgen;

import fun.fengwk.convention4j.common.lifecycle.LifeCycle;
import fun.fengwk.convention4j.common.runtimex.RuntimeLifeCycleException;

/**
 * 使用命名空间相互隔离的ID生成器。
 *
 * @author fengwk
 */
public interface NamespaceIdGenerator<ID> extends LifeCycle {

    /**
     * 生成指定命名空间的下一个id。
     *
     * @param namespace
     * @return
     * @throws RuntimeLifeCycleException 如果当前NamespaceIdGenerator没有处于{@link fun.fengwk.convention4j.common.lifecycle.LifeCycleState#STARTED}状态将会抛出该异常。
     */
    ID next(String namespace);

    /**
     * 使用类的全路径作为命名空间生产下一个id。
     *
     * @param namespace
     * @return
     * @throws RuntimeLifeCycleException 如果当前NamespaceIdGenerator没有处于{@link fun.fengwk.convention4j.common.lifecycle.LifeCycleState#STARTED}状态将会抛出该异常。
     */
    default ID next(Class<?> namespace) {
        return next(namespace.getName());
    }

}

