package fun.fengwk.convention4j.springboot.starter.persistence;

import fun.fengwk.automapper.annotation.UpdateIncrement;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 规约持久化对象。
 *
 * @author fengwk
 */
@Data
public class ConventionPO<ID> extends BasePO<ID> {

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

    /**
     * 逻辑删除位，0-未删除，1-已删除。
     */
    private Integer deleted;

}
