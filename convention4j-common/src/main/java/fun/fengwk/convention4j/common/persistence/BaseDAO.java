package fun.fengwk.convention4j.common.persistence;

import fun.fengwk.convention4j.common.idgen.NamespaceIdGenerator;
import fun.fengwk.convention4j.common.reflect.TypeResolver;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author fengwk
 */
public abstract class BaseDAO<ID, DO extends BaseDO<ID>, MAPPER extends BaseMapper<ID, DO>> {

    protected final NamespaceIdGenerator<ID> idGenerator;
    protected final MAPPER mapper;
    protected final String doTypeName;

    protected BaseDAO(NamespaceIdGenerator<ID> idGenerator, MAPPER mapper) {
        this.idGenerator = idGenerator;
        this.mapper = mapper;
        ParameterizedType parameterizedType = new TypeResolver(getClass()).as(BaseDAO.class).asParameterizedType();
        this.doTypeName = parameterizedType.getActualTypeArguments()[1].getTypeName();
    }

    /**
     * 生成一个新的主键
     */
    public ID generateId() {
        return idGenerator.next(doTypeName);
    }

    /**
     * 插入记录
     *
     * @param record
     * @return
     */
    public int insert(DO record) {
        if (record == null) {
            return 0;
        }
        return mapper.insert(record);
    }

    /**
     * 批量插入记录
     *
     * @param records
     * @return
     */
    public int insertAll(Collection<DO> records) {
        if (records == null || records.isEmpty()) {
            return 0;
        }
        return mapper.insertAll(records);
    }

    /**
     * 删除记录
     *
     * @param id
     * @return
     */
    public int deleteById(ID id) {
        if (id == null) {
            return 0;
        }
        return mapper.deleteById(id);
    }

    /**
     * 批量删除记录
     *
     * @param ids
     * @return
     */
    public int deleteByIds(Collection<ID> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return mapper.deleteByIdIn(ids);
    }

    /**
     * 更新记录
     *
     * @param record
     * @return
     */
    public int updateById(DO record) {
        if (record == null) {
            return 0;
        }
        return mapper.updateById(record);
    }

    /**
     * 选择性更新记录的非空字段
     *
     * @param record
     * @return
     */
    public int updateByIdSelective(DO record) {
        if (record == null) {
            return 0;
        }
        return mapper.updateByIdSelective(record);
    }

    /**
     * 检查指定主键的记录是否存在
     *
     * @param id
     * @return
     */
    public boolean exists(ID id) {
        if (id == null) {
            return false;
        }
        return mapper.countById(id) > 0;
    }

    /**
     * 通过主键获取记录
     *
     * @param id
     * @return
     */
    public DO getById(ID id) {
        if (id == null) {
            return null;
        }
        return mapper.findById(id);
    }

    /**
     * 通过主键集合获取记录列表
     *
     * @param ids
     * @return
     */
    public List<DO> listByIds(Collection<ID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return mapper.findByIdIn(ids);
    }

}
