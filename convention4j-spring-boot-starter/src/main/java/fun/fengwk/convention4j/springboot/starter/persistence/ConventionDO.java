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

    private static final long DEFAULT_VERSION = 0L;

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

    /**
     * 填充初始化字段
     */
    public void populateDefaultFields() {
        setVersion(DEFAULT_VERSION);
        LocalDateTime now = LocalDateTime.now();
        setCreateTime(now);
        setUpdateTime(now);
    }
    
}
