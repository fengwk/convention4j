package fun.fengwk.convention4j.springboot.test.starter.repo;

import fun.fengwk.convention4j.api.page.Page;
import fun.fengwk.convention4j.api.page.PageQuery;
import fun.fengwk.convention4j.common.json.JsonUtils;
import fun.fengwk.convention4j.common.page.Pages;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ResolvableType;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Comparator;
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
            ID id = getId(entity);
            if (updater.apply(entity)) {
                entityMap.remove(id);
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

    protected List<ENTITY> doList(Predicate<ENTITY> predicate, Comparator<ENTITY> order) {
        return entityMap.values().stream()
            .sorted(order).filter(predicate).map(this::copy).collect(Collectors.toList());
    }

    protected long doCount(Predicate<ENTITY> predicate) {
        return entityMap.values().stream()
            .filter(predicate).map(this::copy).count();
    }

    protected Page<ENTITY> doPage(PageQuery pageQuery, Predicate<ENTITY> predicate) {
        long offset = Pages.queryOffset(pageQuery);
        int limit = Pages.queryLimit(pageQuery);
        List<ENTITY> result = doList(predicate).stream().skip(offset).limit(limit).toList();
        long count = doCount(predicate);
        return Pages.page(pageQuery, result, count);
    }

    protected Page<ENTITY> doPage(PageQuery pageQuery, Predicate<ENTITY> predicate, Comparator<ENTITY> order) {
        long offset = Pages.queryOffset(pageQuery);
        int limit = Pages.queryLimit(pageQuery);
        List<ENTITY> result = doList(predicate, order).stream().skip(offset).limit(limit).toList();
        long count = doCount(predicate);
        return Pages.page(pageQuery, result, count);
    }

    private ENTITY copy(ENTITY entity) {
        if (entity == null) {
            return null;
        }
        Type type = ResolvableType.forClass(getClass()).as(AbstractTestRepository.class).getGeneric(0).getType();
        return JsonUtils.fromJson(JsonUtils.toJson(entity), type);
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
