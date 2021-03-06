package fun.fengwk.convention4j.common.lifecycle;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static fun.fengwk.convention4j.common.lifecycle.LifeCycleState.CLOSED;
import static fun.fengwk.convention4j.common.lifecycle.LifeCycleState.CLOSING;
import static fun.fengwk.convention4j.common.lifecycle.LifeCycleState.FAILED;
import static fun.fengwk.convention4j.common.lifecycle.LifeCycleState.FAILING;
import static fun.fengwk.convention4j.common.lifecycle.LifeCycleState.INITIALIZED;
import static fun.fengwk.convention4j.common.lifecycle.LifeCycleState.INITIALIZING;
import static fun.fengwk.convention4j.common.lifecycle.LifeCycleState.STARTED;
import static fun.fengwk.convention4j.common.lifecycle.LifeCycleState.STARTING;
import static fun.fengwk.convention4j.common.lifecycle.LifeCycleState.STOPPED;
import static fun.fengwk.convention4j.common.lifecycle.LifeCycleState.STOPPING;

/**
 * @author fengwk
 */
public abstract class AbstractListenableLifeCycle extends AbstractLifeCycle implements ListenableLifeCycle {

    private final List<LifeCycleListener> listeners = new CopyOnWriteArrayList<>();

    @Override
    public void addLifeCycleListener(LifeCycleListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeLifeCycleListener(LifeCycleListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void removeAllLifeCycleListener() {
        listeners.clear();
    }

    @Override
    protected void onInitializing() {
        super.onInitializing();
        doListen(INITIALIZING);
    }

    @Override
    protected void onInitialized() {
        super.onInitialized();
        doListen(INITIALIZED);
    }

    @Override
    protected void onStarting() {
        super.onStarting();
        doListen(STARTING);
    }

    @Override
    protected void onStarted() {
        super.onStarted();
        doListen(STARTED);
    }

    @Override
    protected void onStopping() {
        super.onStopping();
        doListen(STOPPING);
    }

    @Override
    protected void onStopped() {
        super.onStopped();
        doListen(STOPPED);
    }

    @Override
    protected void onClosing() {
        super.onClosing();
        doListen(CLOSING);
    }

    @Override
    protected void onClosed() {
        super.onClosed();
        doListen(CLOSED);
    }

    @Override
    protected void onFailing() {
        super.onFailing();
        doListen(FAILING);
    }

    @Override
    protected void onFailed() {
        super.onFailed();
        doListen(FAILED);
    }

    /**
     * ?????????????????????
     *
     * @param state
     */
    private void doListen(LifeCycleState state) {
        for (LifeCycleListener listener : listeners) {
            listener.listen(state);
        }
    }

}
