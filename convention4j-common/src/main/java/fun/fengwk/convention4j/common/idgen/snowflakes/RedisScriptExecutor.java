package fun.fengwk.convention4j.common.idgen.snowflakes;

import java.util.List;

/**
 * RedisEvaluator是对于redis eval操作的抽象，允许使用不同的方式实现eval操作。
 *
 * @author fengwk
 */
public interface RedisScriptExecutor extends AutoCloseable {

    /**
     * 向redis发送LUA脚本并执行。
     *
     * @param script
     * @param keys
     * @param args
     * @param returnType
     * @param <T>
     * @return
     * @throws Exception
     */
    <T> T execute(String script, List<String> keys, List<String> args, Class<T> returnType) throws Exception;

}
