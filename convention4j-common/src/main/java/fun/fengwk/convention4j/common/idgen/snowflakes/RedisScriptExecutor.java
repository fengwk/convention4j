package fun.fengwk.convention4j.common.idgen.snowflakes;

import fun.fengwk.convention4j.common.lifecycle.LifeCycle;
import fun.fengwk.convention4j.common.runtimex.RuntimeLifeCycleException;

import java.util.List;

/**
 * RedisEvaluator是对于redis eval操作的抽象，允许使用不同的方式实现eval操作。
 *
 * @author fengwk
 */
public interface RedisScriptExecutor extends LifeCycle {

    /**
     * 向redis发送LUA脚本并执行。
     *
     * @param script
     * @param keys
     * @param args
     * @param returnType
     * @param <T>
     * @return
     * @throws RuntimeLifeCycleException 如果当前WorkerIdClient没有处于{@link fun.fengwk.convention4j.common.lifecycle.LifeCycleState#STARTED}状态将会抛出该异常。
     * @throws Exception
     */
    <T> T execute(String script, List<String> keys, List<String> args, Class<T> returnType) throws Exception;

}
