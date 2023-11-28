package fun.fengwk.convention4j.springboot.starter.persistence;

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
    private LocalDateTime gmtCreate;

    /**
     * 修改时间。
     */
    private LocalDateTime gmtModified;

    /**
     * 数据版本号。
     */
    @UpdateIncrement
    private Long version;

}
