package fun.fengwk.convention4j.springboot.starter.cache.mapper;

import fun.fengwk.convention4j.springboot.starter.cache.annotation.IdKey;
import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class BaseCacheDO<ID> {

    @IdKey
    private ID id;

}
