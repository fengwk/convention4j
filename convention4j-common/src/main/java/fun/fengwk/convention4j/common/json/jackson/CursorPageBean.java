package fun.fengwk.convention4j.common.json.jackson;

import lombok.Data;

import java.util.List;

/**
 * @author fengwk
 */
@Data
public class CursorPageBean<T, C> {

    private C cursor;
    private int limit;
    private List<T> results;
    private C nextCursor;
    private boolean more;

}
