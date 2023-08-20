package fun.fengwk.convention4j.api.page;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 分页查询器。
 *
 * @author fengwk
 */
public class SortablePageQuery extends PageQuery {

    private static final long serialVersionUID = 1L;

    private final List<Sort> sorts;

    public SortablePageQuery(int pageNumber, int pageSize, List<Sort> sorts) {
        super(pageNumber, pageSize);
        this.sorts = sorts == null ? Collections.emptyList() : sorts;
    }

    public static SortablePageQuery buildWithPermittedKeys(
        int pageNumber, int pageSize, String sorts, Collection<String> permittedKeys) {
        List<Sort> sortList = Sort.parseSorts(sorts);
        if (!validateSortKeys(sortList, permittedKeys)) {
            throw new IllegalArgumentException("sorts contains illegal key");
        }
        return new SortablePageQuery(pageNumber, pageSize, sortList);
    }

    public static <E extends Enum<E>> SortablePageQuery buildWithPermittedKeyEnum(
        int pageNumber, int pageSize, String sorts, Class<E> permittedKeyEnum) {
        List<Sort> sortList = Sort.parseSorts(sorts);
        if (!validateSortKeys(sortList, permittedKeyEnum)) {
            throw new IllegalArgumentException("sorts contains illegal key");
        }
        return new SortablePageQuery(pageNumber, pageSize, sortList);
    }

    private static boolean validateSortKeys(List<Sort> sorts, Collection<String> permittedKeys) {
        if (sorts == null) {
            return true;
        }
        for (Sort sort : sorts) {
            if (!permittedKeys.contains(sort.getKey())) {
                return false;
            }
        }
        return true;
    }

    private static <E extends Enum<E>> boolean validateSortKeys(List<Sort> sorts, Class<E> permittedKeyEnumClass) {
        if (permittedKeyEnumClass == null) {
            throw new IllegalArgumentException("permittedKeyEnumClass is null");
        }
        Enum<?>[] enums = permittedKeyEnumClass.getEnumConstants();
        if (enums == null || enums.length == 0) {
            throw new IllegalArgumentException("permittedKeyEnum is empty");
        }
        List<String> permittedKeys = Arrays.stream(enums).map(Enum::name).collect(Collectors.toList());
        return validateSortKeys(sorts, permittedKeys);
    }

    public List<Sort> getSorts() {
        return sorts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SortablePageQuery that = (SortablePageQuery) o;
        return Objects.equals(sorts, that.sorts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sorts);
    }

    @Override
    public String toString() {
        return "SortablePageQuery{" +
            "sorts=" + sorts +
            "} " + super.toString();
    }

}
