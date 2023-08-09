package fun.fengwk.convention4j.api.page;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

    public SortablePageQuery(int pageNumber, int pageSize, String sorts) {
        super(pageNumber, pageSize);
        this.sorts = Sort.parseSorts(sorts);
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
