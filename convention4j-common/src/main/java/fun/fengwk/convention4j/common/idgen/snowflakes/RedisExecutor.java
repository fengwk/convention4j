package fun.fengwk.convention4j.common.idgen.snowflakes;

import fun.fengwk.convention4j.common.runtimex.RuntimeLifeCycleException;

import java.util.List;

/**
 * RedisEvaluator是对于redis eval操作的抽象，允许使用不同的方式实现eval操作。
 *
 * @author fengwk
 */
public interface RedisExecutor {

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
    @Deprecated
    <T> T execute(String script, List<String> keys, List<String> args, Class<T> returnType) throws Exception;

    /**
     * 获取key对应的value
     *
     * @param key key
     * @return value
     */
    String get(String key);

    /**
     * 如果不存在则设置
     *
     * @param key key
     * @param value value
     * @param timeoutMs 超时毫秒
     * @return 是否成功
     */
    boolean setIfAbsent(String key, String value, long timeoutMs);

    /**
     * 超时时间
     *
     * @param key key
     * @param timeoutMs 超时毫秒
     * @return 是否成功
     */
    boolean expire(String key, long timeoutMs);

}
