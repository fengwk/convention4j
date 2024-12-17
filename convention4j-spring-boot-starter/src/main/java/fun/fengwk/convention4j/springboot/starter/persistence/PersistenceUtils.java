package fun.fengwk.convention4j.springboot.starter.persistence;

import fun.fengwk.convention4j.common.idgen.NamespaceIdGenerator;

import java.time.LocalDateTime;

/**
 * @author fengwk
 */
public class PersistenceUtils {

    private static final long DEFAULT_VERSION = 0L;

    private PersistenceUtils() {}

    public static <ID> void populateDefaultFieldsForInsert(ConventionDO<ID> conventionDO, ID id) {
        conventionDO.setId(id);
        conventionDO.setVersion(DEFAULT_VERSION);
        LocalDateTime now = LocalDateTime.now();
        conventionDO.setCreateTime(now);
        conventionDO.setUpdateTime(now);
    }

    public static <ID> void populateDefaultFieldsForInsert(ConventionDO<ID> conventionDO,
                                                           NamespaceIdGenerator<ID> idGenerator) {
        populateDefaultFieldsForInsert(conventionDO, idGenerator.next(conventionDO.getClass()));
    }

    public static <ID> void populateDefaultFieldsForUpdate(ConventionDO<ID> conventionDO) {
        LocalDateTime now = LocalDateTime.now();
        conventionDO.setUpdateTime(now);
    }

}
