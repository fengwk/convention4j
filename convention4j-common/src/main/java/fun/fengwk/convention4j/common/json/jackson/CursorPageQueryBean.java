package fun.fengwk.convention4j.common.json.jackson;

import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class CursorPageQueryBean<C> {

    private C cursor;
    private int limit;

}
