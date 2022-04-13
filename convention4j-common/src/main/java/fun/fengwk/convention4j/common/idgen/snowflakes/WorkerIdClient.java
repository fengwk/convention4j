package fun.fengwk.convention4j.common.idgen.snowflakes;

import fun.fengwk.convention4j.common.idgen.ClosedException;
import fun.fengwk.convention4j.common.idgen.LifeCycle;

import java.util.concurrent.TimeUnit;

/**
 * 接口用于获取workerId。
 *
 * @author fengwk
 */
public interface WorkerIdClient extends LifeCycle {

    /**
     * 阻塞获取一个在[0, 1024)区间内的工作节点编号，直到成功为止。
     *
     * @return
     * @throws ClosedException 如果客户端已被关闭，将抛出该异常。
     */
    long get() throws ClosedException;

    /**
     * 可中断地阻塞获取一个在[0, 1024)区间内的工作节点编号。
     *
     * @return
     * @throws InterruptedException 如果阻塞获取被终端，将抛出该异常。
     * @throws ClosedException 如果客户端已被关闭，将抛出该异常。
     */
    long getInterruptibly() throws InterruptedException, ClosedException;

    /**
     * 尝试获取一个在[0, 1024)区间内的工作节点编号，如果失败将返回null。
     *
     * @return
     * @throws ClosedException 如果客户端已被关闭，将抛出该异常。
     */
    Long tryGet() throws ClosedException;

    /**
     * 在一定超时时间内可中断地阻塞获取一个在[0, 1024)区间内的工作节点编号。
     *
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException 如果阻塞获取被终端，将抛出该异常。
     * @throws ClosedException 如果客户端已被关闭，将抛出该异常。
     */
    Long tryGet(long timeout, TimeUnit unit) throws InterruptedException, ClosedException;

}
