package fun.fengwk.convention4j.common.lifecycle;

/**
 * 可监听的生命周期状态。
 *
 * @see LifeCycleState
 * @author fengwk
 */
public interface ListenableLifeCycleState {

    /**
     * 添加指定监听器。
     *
     * @param listener
     */
    void addLifeCycleListener(LifeCycleListener listener);

    /**
     * 移除指定监听器。
     *
     * @param listener
     */
    void removeLifeCycleListener(LifeCycleListener listener);

    /**
     * 移除所有监听器。
     */
    void removeAllLifeCycleListener();

}
