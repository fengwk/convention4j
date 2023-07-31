package fun.fengwk.convention4j.common.persistence;

import java.util.Collection;

/**
 * @author fengwk
 */
public interface BaseMysqlMapper<ID, DO extends BaseDO<ID>> extends BaseMapper<ID, DO> {

    int insertIgnore(DO record);

    int insertIgnoreAll(Collection<DO> records);

}
