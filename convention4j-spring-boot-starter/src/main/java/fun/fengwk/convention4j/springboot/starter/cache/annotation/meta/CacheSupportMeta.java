package fun.fengwk.convention4j.springboot.starter.cache.annotation.meta;

import fun.fengwk.convention4j.springboot.starter.cache.annotation.provider.WriteTransactionSupport;
import lombok.Data;

/**
 * @author fengwk
 * @see fun.fengwk.convention4j.springboot.starter.cache.annotation.CacheSupport
 */
@Data
public class CacheSupportMeta {

    private final String version;
    private final int expireSeconds;
    private final Class<?> objClass;
    private final Class<? extends WriteTransactionSupport> writeTransactionSupport;

}
