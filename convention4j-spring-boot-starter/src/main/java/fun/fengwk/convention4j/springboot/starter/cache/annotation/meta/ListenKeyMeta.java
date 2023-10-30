package fun.fengwk.convention4j.springboot.starter.cache.annotation.meta;

import lombok.Data;

/**
 * @author fengwk
 * @see fun.fengwk.convention4j.springboot.starter.cache.annotation.ListenKey
 */
@Data
public class ListenKeyMeta {

    private final String value;
    private final boolean required;

}
