package fun.fengwk.convention4j.springboot.starter.cache.meta;

import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class CacheConfigMeta {

    private final String version;
    private final int expireSeconds;

}
