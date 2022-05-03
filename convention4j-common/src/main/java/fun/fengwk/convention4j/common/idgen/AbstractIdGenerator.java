package fun.fengwk.convention4j.common.idgen;

import fun.fengwk.convention4j.common.lifecycle.AbstractLifeCycle;
import fun.fengwk.convention4j.common.runtimex.RuntimeLifeCycleException;

import static fun.fengwk.convention4j.common.lifecycle.LifeCycleState.STARTED;

/**
 * @author fengwk
 */
public abstract class AbstractIdGenerator<ID> extends AbstractLifeCycle implements IdGenerator<ID> {

    @Override
    public ID next() {
        getLifeCycleRwLock().readLock().lock();
        try {
            if (getState() != STARTED) {
                throw new RuntimeLifeCycleException(String.format("%s state is not %s",
                        getClass().getSimpleName(), STARTED));
            }

            return doNext();

        } finally {
            getLifeCycleRwLock().readLock().unlock();
        }
    }

    /**
     * 生成下一个id。
     *
     * @return
     * @throws RuntimeLifeCycleException 如果当前IdGenerator没有处于{@link fun.fengwk.convention4j.common.lifecycle.LifeCycleState#STARTED}状态将会抛出该异常。
     */
    protected abstract ID doNext();

}
