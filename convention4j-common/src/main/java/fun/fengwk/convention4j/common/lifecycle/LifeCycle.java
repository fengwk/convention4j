package fun.fengwk.convention4j.common.lifecycle;

/**
 * 生命周期，该接口的实现类应当保证线程安全的生命周期实现。
 *
 * @author fengwk
 */
public interface LifeCycle {

    /**
     * 初始化，这一步骤通常做一些准备工作，比如确认资源是否存在，加载资源到内存等。
     * 只有当前状态为{@link LifeCycleState#NEW}时才会执行此操作，
     * 如果确实执行了{@link #init()}将返回true，否则返回false。
     * 如果初始化过程发生错误将抛出{@link LifeCycleException}，进入{@link LifeCycleState#FAILED}状态且尝试进行关闭。
     *
     * @return
     * @throws LifeCycleException
     */
    boolean init() throws LifeCycleException;

    /**
     * 启动，这一步骤通常会使用初始化准备的内容进行启动。
     * 只有当前状态为{@link LifeCycleState#INITIALIZED}或{@link LifeCycleState#STOPPED}时才会执行此操作，
     * 如果确实执行了{@link #start()}将返回true，否则返回false。
     * 如果启动过程发生错误将抛出{@link LifeCycleException}，进入{@link LifeCycleState#FAILED}状态且尝试进行停止和关闭。
     *
     * @return
     * @throws LifeCycleException
     */
    boolean start() throws LifeCycleException;

    /**
     * 停止，这一步骤可以停止运行，但通常不会释放资源，以便可以快速重启动。
     * 只有当前状态为{@link LifeCycleState#STARTED}时才会执行此操作，
     * 如果确实执行了{@link #stop()}将返回true，否则返回false。
     * 如果停止过程发生错误将抛出{@link LifeCycleException}，进入{@link LifeCycleState#FAILED}状态且尝试进行关闭。
     *
     * @throws LifeCycleException
     */
    boolean stop() throws LifeCycleException;

    /**
     * 闭并释放资源，一旦被关闭将无法重新启用，{@link LifeCycleState#NEW}、{@link LifeCycleState#INITIALIZED}、
     * {@link LifeCycleState#STOPPED}中的任一状态都能进行关闭。
     * 如果关闭过程发生错误将抛出{@link LifeCycleException}，并且进入{@link LifeCycleState#FAILED}状态。
     *
     * @throws LifeCycleException
     */
    boolean close() throws LifeCycleException;

    /**
     * 获取当前生命周期状态。
     *
     * @return
     */
    LifeCycleState getState();

}
