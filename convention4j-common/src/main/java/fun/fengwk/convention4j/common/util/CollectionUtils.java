package fun.fengwk.convention4j.common.util;

import java.util.Collection;

/**
 * @author fengwk
 */
public class CollectionUtils {

    private CollectionUtils() {}

    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> boolean isNotEmpty(Collection<T> collection) {
        return collection != null && !collection.isEmpty();
    }

}
