package fun.fengwk.convention4j.api.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 排序键，允许使用以下方式继续序列化，{@code "username,age+,createTime-"}，其中{@code +}或无符号表示升序，{@code -}表示降序。
 *
 * @author fengwk
 */
public class Sort {

    private static final String SEPARATOR = ",";
    private static final String ASC = "+";
    private static final String DESC = "-";

    /**
     * 排序键
     */
    private final String key;

    /**
     * true-升序，false-降序
     */
    private final boolean asc;

    public Sort(String key, boolean asc) {
        this.key = key;
        this.asc = asc;
    }

    public static List<Sort> parseSorts(String sortStr) {
        if (sortStr == null || sortStr.isEmpty()) {
            return Collections.emptyList();
        }

        List<Sort> sorts = new ArrayList<>();
        String[] parts = sortStr.split(SEPARATOR);
        for (String part : parts) {
            Sort sort = parseSort(part);
            if (sort != null) {
                sorts.add(sort);
            }
        }
        return sorts;
    }

    public static Sort parseSort(String sortStr) {
        sortStr = sortStr.trim();
        if (sortStr.isEmpty()) {
            return null;
        }

        boolean asc = !sortStr.endsWith(DESC);
        if (asc) {
            if (sortStr.endsWith(ASC)) {
                sortStr = sortStr.substring(0, sortStr.length() - ASC.length());
            }
        } else {
            sortStr = sortStr.substring(0, sortStr.length() - DESC.length());
        }
        return new Sort(sortStr, asc);
    }

    public String getKey() {
        return key;
    }

    public boolean isAsc() {
        return asc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sort sort = (Sort) o;
        return asc == sort.asc && Objects.equals(key, sort.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, asc);
    }

    @Override
    public String toString() {
        return key + (asc ? ASC : DESC);
    }

}
