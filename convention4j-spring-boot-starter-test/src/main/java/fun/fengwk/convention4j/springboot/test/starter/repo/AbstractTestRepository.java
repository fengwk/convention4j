package fun.fengwk.convention4j.springboot.test.starter.repo;

import fun.fengwk.convention4j.common.gson.GsonUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
public abstract class AbstractTestRepository<ENTITY, ID> {

    protected final ConcurrentMap<ID, ENTITY> entityMap = new ConcurrentHashMap<>();

    protected abstract ID getId(ENTITY entity);

    protected boolean doInsert(ENTITY entity) {
        if (entity == null) {
            return false;
        }
        ENTITY copiedEntity = copy(entity);
        ID id = getId(copiedEntity);
        return entityMap.putIfAbsent(id, copiedEntity) == null;
    }

    protected int doInsertAll(Collection<ENTITY> entities) {
        int inserted = 0;
        for (ENTITY entity : entities) {
            if (doInsert(entity)) {
                inserted++;
            }
        }
        return inserted;
    }

    protected boolean doDeleteById(ID id) {
        return entityMap.remove(id) != null;
    }

    protected boolean doUpdateById(ENTITY entity) {
        if (entity == null) {
            return false;
        }
        ENTITY copiedEntity = copy(entity);
        ID id = getId(copiedEntity);
        return entityMap.computeIfPresent(id, (k, v) -> copiedEntity) != null;
    }

    protected ENTITY doGetById(ID id) {
        return copy(entityMap.get(id));
    }

    protected int doDelete(Predicate<ENTITY> predicate) {
        int deleted = 0;
        for (ENTITY entity : doList(predicate)) {
            ID id = getId(entity);
            if (doDeleteById(id)) {
                deleted++;
            }
        }
        return deleted;
    }

    protected int doUpdate(Predicate<ENTITY> predicate, Function<ENTITY, Boolean> updater) {
        int updated = 0;
        for (ENTITY entity : doList(predicate)) {
            if (updater.apply(entity)) {
                entityMap.put(getId(entity), entity);
                updated++;
            }
        }
        return updated;
    }

    protected int doUpdate(Predicate<ENTITY> predicate, Supplier<ENTITY> newEntitySupplier) {
        return doUpdate(predicate, entity -> {
            ENTITY newEntity = newEntitySupplier.get();
            if (newEntity == null) {
                return false;
            }
            copyProperties(newEntity, entity);
            return true;
        });
    }

    protected int doUpdateSelective(Predicate<ENTITY> predicate, Supplier<ENTITY> newEntitySupplier) {
        return doUpdate(predicate, entity -> {
            ENTITY newEntity = newEntitySupplier.get();
            if (newEntity == null) {
                return false;
            }
            copyPropertiesSelective(newEntity, entity);
            return true;
        });
    }

    protected ENTITY doGet(Predicate<ENTITY> predicate) {
        return entityMap.values().stream()
            .filter(predicate).findFirst()
            .map(this::copy).orElse(null);
    }

    protected List<ENTITY> doList(Predicate<ENTITY> predicate) {
        return entityMap.values().stream()
            .filter(predicate).map(this::copy).collect(Collectors.toList());
    }

    private ENTITY copy(ENTITY entity) {
        if (entity == null) {
            return null;
        }
        return (ENTITY) GsonUtils.fromJson(
            GsonUtils.toJson(entity), entity.getClass());
    }

    private void copyProperties(ENTITY from, ENTITY to) {
        BeanUtils.copyProperties(from, to);
    }

    private void copyPropertiesSelective(ENTITY from, ENTITY to) {
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(from.getClass());
        for (PropertyDescriptor pd : pds) {
            Method readMethod = pd.getReadMethod();
            Method writeMethod = pd.getWriteMethod();
            if (readMethod != null && writeMethod != null) {
                Object value = ReflectionUtils.invokeMethod(readMethod, from);
                if (value != null) {
                    ReflectionUtils.invokeMethod(writeMethod, to, value);
                }
            }
        }
    }

}
