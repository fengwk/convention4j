package fun.fengwk.convention4j.common.lifecycle;

/**
 * 生命周期监听器，用于监听生命周期状态。
 *
 * @author fengwk
 */
@FunctionalInterface
public interface LifeCycleListener {

    /**
     * 当某一{@link LifeCycleState}发生时将产生回调。
     *
     * @param state
     */
    void listen(LifeCycleState state);

}
