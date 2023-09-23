package fun.fengwk.convention4j.springboot.test.starter.repo;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
public abstract class AbstractTestRepository<ENTITY, ID> {

    protected final ConcurrentMap<ID, ENTITY> entityMap = new ConcurrentHashMap<>();

    protected abstract ID getId(ENTITY entity);

    public boolean insert(ENTITY entity) {
        if (entity == null) {
            return false;
        }
        ID id = getId(entity);
        return entityMap.putIfAbsent(id, entity) == null;
    }

    public boolean deleteById(ID id) {
        return entityMap.remove(id) != null;
    }

    public boolean updateById(ENTITY entity) {
        if (entity == null) {
            return false;
        }
        ID id = getId(entity);
        return entityMap.computeIfPresent(id, (k, v) -> entity) != null;
    }

    public ENTITY getById(ID id) {
        return entityMap.get(id);
    }

    protected int delete(Predicate<ENTITY> predicate) {
        int deleted = 0;
        for (ENTITY entity : list(predicate)) {
            ID id = getId(entity);
            if (deleteById(id)) {
                deleted++;
            }
        }
        return deleted;
    }

    protected int update(Predicate<ENTITY> predicate) {
        int updated = 0;
        for (ENTITY entity : list(predicate)) {
            if (updateById(entity)) {
                updated++;
            }
        }
        return updated;
    }

    protected ENTITY get(Predicate<ENTITY> predicate) {
        return entityMap.values().stream()
            .filter(predicate).findFirst().orElse(null);
    }

    protected List<ENTITY> list(Predicate<ENTITY> predicate) {
        return entityMap.values().stream()
            .filter(predicate).collect(Collectors.toList());
    }

}
