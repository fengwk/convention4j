package fun.fengwk.convention4j.springboot.starter.persistence;

import fun.fengwk.automapper.annotation.OnDuplicateKeyUpdateIgnore;
import fun.fengwk.automapper.annotation.UpdateIncrement;
import fun.fengwk.convention4j.common.util.DateUtils;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;

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
     * 填充初始化字段
     */
    public void populateDefaultFields() {
        LocalDateTime now = LocalDateTime.now(DateUtils.zoneIdUTC());
        setGmtCreate(now);
        setGmtModified(now);
        setVersion(DEFAULT_VERSION);
    }

    /**
     * 获取目标ZoneId的创建时间
     *
     * @param zoneId 创建时间转为目标的ZoneId
     * @return 目标ZoneId的创建时间
     */
    public LocalDateTime getCreateTime(ZoneId zoneId) {
        return DateUtils.convertTimeZone(getGmtCreate(), DateUtils.zoneIdUTC(), zoneId);
    }

    /**
     * 获取目标ZoneId的更新时间
     *
     * @param zoneId 更新时间转为目标的ZoneId
     * @return 目标ZoneId的更新时间
     */
    public LocalDateTime getModifiedTime(ZoneId zoneId) {
        return DateUtils.convertTimeZone(getGmtModified(), DateUtils.zoneIdUTC(), zoneId);
    }

    /**
     * 获取系统默认ZoneId的创建时间
     *
     * @return 系统默认ZoneId的创建时间
     */
    public LocalDateTime getCreateTime() {
        return getCreateTime(ZoneId.systemDefault());
    }

    /**
     * 获取系统默认ZoneId的更新时间
     *
     * @return 系统默认ZoneId的更新时间
     */
    public LocalDateTime getModifiedTime() {
        return getModifiedTime(ZoneId.systemDefault());
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     * @param zoneId 创建时间的ZoneId
     */
    public void setCreateTime(LocalDateTime createTime, ZoneId zoneId) {
        setGmtCreate(DateUtils.convertTimeZone(createTime, zoneId, DateUtils.zoneIdUTC()));
    }

    /**
     * 设置更新时间
     *
     * @param modifiedTime 更新时间
     * @param zoneId 更新时间的ZoneId
     */
    public void setModifiedTime(LocalDateTime modifiedTime, ZoneId zoneId) {
        setGmtModified(DateUtils.convertTimeZone(modifiedTime, zoneId, DateUtils.zoneIdUTC()));
    }

    /**
     * 设置创建时间，创建时间的时区为系统默认
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(LocalDateTime createTime) {
        setCreateTime(createTime, ZoneId.systemDefault());
    }

    /**
     * 设置更新时间，更新时间的时区为系统默认
     *
     * @param modifiedTime 更新时间
     */
    public void setModifiedTime(LocalDateTime modifiedTime) {
        setModifiedTime(modifiedTime, ZoneId.systemDefault());
    }

}
