package fun.fengwk.convention4j.common.persistence;

import fun.fengwk.convention4j.common.idgen.NamespaceIdGenerator;

import java.util.Collection;

/**
 * @author fengwk
 */
public class BaseMysqlDAO<ID, DO extends BaseDO<ID>, MAPPER extends BaseMysqlMapper<ID, DO>> extends BaseDAO<ID, DO, MAPPER> {

    protected BaseMysqlDAO(NamespaceIdGenerator<ID> idGenerator, MAPPER mapper) {
        super(idGenerator, mapper);
    }

    public int insertIgnore(DO record) {
        if (record == null) {
            return 0;
        }
        return mapper.insertIgnore(record);
    }

    public int insertIgnoreAll(Collection<DO> records) {
        if (records == null || records.isEmpty()) {
            return 0;
        }
        return mapper.insertIgnoreAll(records);
    }

}
