package fun.fengwk.convention4j.springboot.starter.persistence;

import fun.fengwk.automapper.annotation.OnDuplicateKeyUpdateIgnore;
import fun.fengwk.automapper.annotation.UpdateIncrement;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 规约数据层对象。
 *
 * @author fengwk
 */
@Data
public abstract class ConventionDO<ID> extends BaseDO<ID> {

    /**
     * 创建时间。
     */
    @OnDuplicateKeyUpdateIgnore
    private LocalDateTime createTime;

    /**
     * 修改时间。
     */
    private LocalDateTime updateTime;

    /**
     * 数据版本号。
     */
    @UpdateIncrement
    private Long version;

}
