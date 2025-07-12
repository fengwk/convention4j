package fun.fengwk.convention4j.common.idgen.uuid;

import fun.fengwk.convention4j.common.idgen.AbstractIdGenerator;
import fun.fengwk.convention4j.common.idgen.IdGenUtils;
import fun.fengwk.convention4j.common.lifecycle.LifeCycleException;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 该生成器将生成32位的UUID。
 * 
 * @author fengwk
 */
public class UUIDGenerator extends AbstractIdGenerator<String> {

    @Override
    protected ReentrantReadWriteLock getLifeCycleRwLock() {
        return super.getLifeCycleRwLock();
    }

    @Override
    protected String doNext() {
        return IdGenUtils.generateUUID();
    }

    @Override
    protected void doInit() throws LifeCycleException {
        // nothing to do
    }

    @Override
    protected void doStart() throws LifeCycleException {
        // nothing to do
    }

    @Override
    protected void doStop() throws LifeCycleException {
        // nothing to do
    }

    @Override
    protected void doClose() throws LifeCycleException {
        // nothing to do
    }

    @Override
    protected void doFail() throws LifeCycleException {
        // nothing to do
    }

}
