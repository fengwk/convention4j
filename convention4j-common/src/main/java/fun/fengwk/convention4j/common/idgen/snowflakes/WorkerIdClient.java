package fun.fengwk.convention4j.common.idgen.snowflakes;

import fun.fengwk.convention4j.common.lifecycle.LifeCycle;
import fun.fengwk.convention4j.common.runtimex.RuntimeLifeCycleException;

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
     * @throws RuntimeLifeCycleException 如果当前WorkerIdClient没有处于{@link fun.fengwk.convention4j.common.lifecycle.LifeCycleState#STARTED}状态将会抛出该异常。
     */
    long get();

    /**
     * 可中断地阻塞获取一个在[0, 1024)区间内的工作节点编号。
     *
     * @return
     * @throws InterruptedException 如果阻塞获取被终端，将抛出该异常。
     * @throws RuntimeLifeCycleException 如果当前WorkerIdClient没有处于{@link fun.fengwk.convention4j.common.lifecycle.LifeCycleState#STARTED}状态将会抛出该异常。
     */
    long getInterruptibly() throws InterruptedException;

    /**
     * 尝试获取一个在[0, 1024)区间内的工作节点编号，如果失败将返回null。
     *
     * @return
     * @throws RuntimeLifeCycleException 如果当前WorkerIdClient没有处于{@link fun.fengwk.convention4j.common.lifecycle.LifeCycleState#STARTED}状态将会抛出该异常。
     */
    Long tryGet();

    /**
     * 在一定超时时间内可中断地阻塞获取一个在[0, 1024)区间内的工作节点编号。
     *
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException 如果阻塞获取被终端，将抛出该异常。
     * @throws RuntimeLifeCycleException 如果当前WorkerIdClient没有处于{@link fun.fengwk.convention4j.common.lifecycle.LifeCycleState#STARTED}状态将会抛出该异常。
     */
    Long tryGet(long timeout, TimeUnit unit) throws InterruptedException;

}
