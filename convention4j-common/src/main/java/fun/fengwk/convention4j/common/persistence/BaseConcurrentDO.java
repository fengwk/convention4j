package fun.fengwk.convention4j.common.persistence;

import java.util.Objects;

/**
 * 基础的并发持久化对象
 *
 * @author fengwk
 */
public class BaseConcurrentDO<ID> extends BaseDO<ID> {

    /**
     * 数据版本号
     */
    private Long version;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BaseConcurrentDO<?> that = (BaseConcurrentDO<?>) o;
        return Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), version);
    }

    @Override
    public String toString() {
        return "BaseConcurrentDO{" +
            "version=" + version +
            "} " + super.toString();
    }

}
