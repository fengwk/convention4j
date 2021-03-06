package fun.fengwk.convention4j.springboot.starter.snowflake;

import fun.fengwk.convention4j.common.idgen.NamespaceIdGenerator;

/**
 * @author fengwk
 */
public class GlobalSnowflakeIdGenerator {

    private static volatile NamespaceIdGenerator<Long> instance;

    private GlobalSnowflakeIdGenerator() {}

    /**
     * 设置全局SnowflakeIdGenerator实例。
     *
     * @param snowflakeIdGenerator
     */
    static void setInstance(NamespaceIdGenerator<Long> snowflakeIdGenerator) {
        instance = snowflakeIdGenerator;
    }

    public static long next(String namespace) {
        return instance.next(namespace);
    }

    public static long next(Class<?> namespace) {
        return instance.next(namespace);
    }

}
