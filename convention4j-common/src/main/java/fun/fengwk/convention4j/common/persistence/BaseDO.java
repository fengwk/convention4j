package fun.fengwk.convention4j.common.persistence;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 基础的持久化对象
 *
 * @author fengwk
 */
public class BaseDO<ID> {

    /**
     * 主键
     */
    private ID id;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    private LocalDateTime gmtModified;

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseDO<?> baseDO = (BaseDO<?>) o;
        return Objects.equals(id, baseDO.id) && Objects.equals(gmtCreate, baseDO.gmtCreate) && Objects.equals(gmtModified, baseDO.gmtModified);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, gmtCreate, gmtModified);
    }

    @Override
    public String toString() {
        return "BaseDO{" +
            "id=" + id +
            ", gmtCreate=" + gmtCreate +
            ", gmtModified=" + gmtModified +
            '}';
    }

}
