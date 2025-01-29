package fun.fengwk.convention4j.common.lifecycle;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import static fun.fengwk.convention4j.common.lifecycle.LifeCycleState.*;

/**
 * @author fengwk
 */
public abstract class AbstractLifeCycle implements LifeCycle {

    /**
     * 生命周期状态。
     */
    private volatile LifeCycleState state;

    /**
     * 状态码读写锁。
     */
    private final ReentrantReadWriteLock lifeCycleRwLock = new ReentrantReadWriteLock();

    public AbstractLifeCycle() {
        this.state = NEW;
        onNew();
    }

    @Override
    public boolean init() throws LifeCycleException {
        getLifeCycleRwLock().writeLock().lock();
        try {
            LifeCycleState state = getState();

            if (!canInit(state)) {
                return false;
            }

            setState(INITIALIZING);
        } finally {
            getLifeCycleRwLock().writeLock().unlock();
        }

        onInitializing();

        try {
            doInit();
        } catch (LifeCycleException ex) {
            fail(ex);
            throw ex;
        }

        setStateWithLock(INITIALIZED);

        onInitialized();

        return true;
    }

    @Override
    public boolean start() throws LifeCycleException {
        getLifeCycleRwLock().writeLock().lock();
        try {
            LifeCycleState state = getState();

            if (!canStart(state)) {
                return false;
            }

            setState(STARTING);
        } finally {
            getLifeCycleRwLock().writeLock().unlock();
        }

        onStarting();

        try {
            doStart();
        } catch (LifeCycleException ex) {
            fail(ex);
            throw ex;
        }

        setStateWithLock(STARTED);

        onStarted();

        return true;
    }

    @Override
    public boolean stop() throws LifeCycleException {
        getLifeCycleRwLock().writeLock().lock();
        try {
            LifeCycleState state = getState();

            if (!canStop(state)) {
                return false;
            }

            setState(STOPPING);
        } finally {
            getLifeCycleRwLock().writeLock().unlock();
        }

        onStopping();

        try {
            doStop();
        } catch (LifeCycleException ex) {
            fail(ex);
            throw ex;
        }

        setStateWithLock(STOPPED);

        onStopped();

        return true;
    }

    @Override
    public boolean close() throws LifeCycleException {
        stop(); // 先停止

        getLifeCycleRwLock().writeLock().lock();
        try {
            LifeCycleState state = getState();

            if (!canClose(state)) {
                return false;
            }

            setState(CLOSING);
        } finally {
            getLifeCycleRwLock().writeLock().unlock();
        }

        onClosing();

        try {
            doClose();
        } catch (LifeCycleException ex) {
            fail(ex);
            throw ex;
        }

        setStateWithLock(CLOSED);

        onClosed();

        return true;
    }

    @Override
    public LifeCycleState getState() {
        return state;
    }

    protected void setState(LifeCycleState update) {
        state = update;
    }

    protected void setStateWithLock(LifeCycleState update) {
        getLifeCycleRwLock().writeLock().lock();
        try {
            setState(update);
        } finally {
            getLifeCycleRwLock().writeLock().unlock();
        }
    }

    protected ReentrantReadWriteLock getLifeCycleRwLock() {
        return lifeCycleRwLock;
    }

    /**
     * {@link #init()}的具体实现逻辑。
     *
     * @throws LifeCycleException 所有内部异常需要转为{@link LifeCycleException}抛出，否则会影响状态转义，及转移时的执行语义。
     */
    protected abstract void doInit() throws LifeCycleException;

    /**
     * {@link #start()}的具体实现逻辑。
     *
     * @throws LifeCycleException 所有内部异常需要转为{@link LifeCycleException}抛出，否则会影响状态转义，及转移时的执行语义。
     */
    protected abstract void doStart() throws LifeCycleException;

    /**
     * {@link #stop()}的具体实现逻辑。
     *
     * @throws LifeCycleException 所有内部异常需要转为{@link LifeCycleException}抛出，否则会影响状态转义，及转移时的执行语义。
     */
    protected abstract void doStop() throws LifeCycleException;

    /**
     * {@link #close()}的具体实现逻辑。
     *
     * @throws LifeCycleException 所有内部异常需要转为{@link LifeCycleException}抛出，否则会影响状态转义，及转移时的执行语义。
     */
    protected abstract void doClose() throws LifeCycleException;

    /**
     * 失败的具体实现逻辑。
     *
     * @throws LifeCycleException 所有内部异常需要转为{@link LifeCycleException}抛出，否则会影响状态转义，及转移时的执行语义。
     */
    protected abstract void doFail() throws LifeCycleException;

    /**
     * 确保进入当前方法前已经是{@link LifeCycleState#FAILING}状态。
     *
     * @param ex
     */
    private void fail(LifeCycleException ex) throws LifeCycleException {
        setState(FAILING);
        onFailing();

        try {
            doFail();
        } catch (LifeCycleException failingEx) {
            ex.addSuppressed(failingEx);
        } finally {
            setState(FAILED);
            onFailed();
        }
    }

    /**
     * 判断当前状态是否能够执行{@link #init()}。
     *
     * @return
     */
    protected boolean canInit(LifeCycleState state) {
        return state == NEW;
    }

    /**
     * 判断当前状态是否能够执行{@link #start()}。
     *
     * @return
     */
    protected boolean canStart(LifeCycleState state) {
        return state == INITIALIZED || state == STOPPED;
    }

    /**
     * 判断当前状态是否能够执行{@link #stop()}。
     *
     * @return
     */
    protected boolean canStop(LifeCycleState state) {
        return state == STARTED;
    }

    /**
     * 判断当前状态是否能够执行{@link #close()}。
     *
     * @return
     */
    protected boolean canClose(LifeCycleState state) {
        return state == NEW || state == INITIALIZED || state == STOPPED;
    }

    /**
     * 当状态变更为{@link LifeCycleState#NEW}时触发该回调。
     */
    protected void onNew() {}

    /**
     * 当状态变更为{@link LifeCycleState#INITIALIZING}时触发该回调。
     */
    protected void onInitializing() {}

    /**
     * 当状态变更为{@link LifeCycleState#INITIALIZED}时触发该回调。
     */
    protected void onInitialized() {}

    /**
     * 当状态变更为{@link LifeCycleState#STARTING}时触发该回调。
     */
    protected void onStarting() {}

    /**
     * 当状态变更为{@link LifeCycleState#STARTED}时触发该回调。
     */
    protected void onStarted() {}

    /**
     * 当状态变更为{@link LifeCycleState#STOPPING}时触发该回调。
     */
    protected void onStopping() {}

    /**
     * 当状态变更为{@link LifeCycleState#STOPPED}时触发该回调。
     */
    protected void onStopped() {}

    /**
     * 当状态变更为{@link LifeCycleState#CLOSING}时触发该回调。
     */
    protected void onClosing() {}

    /**
     * 当状态变更为{@link LifeCycleState#CLOSED}时触发该回调。
     */
    protected void onClosed() {}

    /**
     * 当状态变更为{@link LifeCycleState#FAILING}时触发该回调。
     */
    protected void onFailing() {}

    /**
     * 当状态变更为{@link LifeCycleState#FAILED}时触发该回调。
     */
    protected void onFailed() {}

}
