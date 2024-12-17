package fun.fengwk.convention4j.springboot.starter.persistence;

import fun.fengwk.automapper.annotation.Id;
import fun.fengwk.automapper.annotation.OnDuplicateKeyUpdateIgnore;
import lombok.Data;

/**
 * 基础的数据层对象。
 *
 * @author fengwk
 */
@Data
public abstract class BaseDO<ID> {

    /**
     * 主键。
     */
    @OnDuplicateKeyUpdateIgnore
    @Id
    private ID id;

}
