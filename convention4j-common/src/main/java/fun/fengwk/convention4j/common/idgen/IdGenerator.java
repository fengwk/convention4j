package fun.fengwk.convention4j.common.idgen;

import fun.fengwk.convention4j.common.lifecycle.LifeCycle;
import fun.fengwk.convention4j.common.runtimex.RuntimeLifeCycleException;

/**
 * {@link IdGenerator}对象能够不断生成下一个ID。
 *
 * @author fengwk
 */
public interface IdGenerator<ID> extends LifeCycle {

    /**
     * 生成下一个id。
     * 
     * @return
     * @throws RuntimeLifeCycleException 如果当前IdGenerator没有处于{@link fun.fengwk.convention4j.common.lifecycle.LifeCycleState#STARTED}状态将会抛出该异常。
     */
    ID next();

}
