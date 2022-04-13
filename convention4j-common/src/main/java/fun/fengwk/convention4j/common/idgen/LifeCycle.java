package fun.fengwk.convention4j.common.idgen;

/**
 * 生命周期。
 *
 * @author fengwk
 */
public interface LifeCycle extends AutoCloseable {

    /**
     * 闭当前对象本身，releaseResource决定是否释放相关联的资源，同时关闭动作应该是幂等的。
     * 一旦close方法被调用，当前客户端就进入关闭状态，此时再调用其它方法将抛出WorkerIdClientClosedException异常。
     *
     * @param releaseResource
     * @throws Exception
     */
    void close(boolean releaseResource) throws Exception;

    /**
     * 闭当前对象本身并释放相关联的资源，与close(true)等效。
     * 一旦close方法被调用，当前客户端就进入关闭状态，此时再调用其它方法将抛出WorkerIdClientClosedException异常。
     *
     * @throws Exception
     */
    @Override
    default void close() throws Exception {
        close(true);
    }

}
