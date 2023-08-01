package fun.fengwk.convention4j.api.page;

import lombok.Data;

import java.io.Serializable;

/**
 * 游标分页查询器。
 *
 * @author fengwk
 */
@Data
public class CursorPageQuery<C> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前游标。
     */
    private final C cursor;

    /**
     * 游标分页限制。
     */
    private final int limit;

}
