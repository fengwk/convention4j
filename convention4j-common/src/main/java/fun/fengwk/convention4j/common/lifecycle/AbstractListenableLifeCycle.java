package fun.fengwk.convention4j.common.lifecycle;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static fun.fengwk.convention4j.common.lifecycle.LifeCycleState.*;

/**
 * @author fengwk
 */
public abstract class AbstractListenableLifeCycle extends AbstractLifeCycle implements ListenableLifeCycle {

    private final List<LifeCycleListener> listeners;

    public AbstractListenableLifeCycle() {
        this(Collections.emptyList());
    }

    public AbstractListenableLifeCycle(Collection<LifeCycleListener> listeners) {
        super();
        this.listeners = new CopyOnWriteArrayList<>(listeners);
        doListen(NEW);
    }

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
    protected void onNew() {
        super.onNew();
        // 在构造器中完成doListen(NEW);
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
     * 进行监听行为。
     *
     * @param state
     */
    private void doListen(LifeCycleState state) {
        for (LifeCycleListener listener : listeners) {
            listener.listen(state);
        }
    }

}
