package fun.fengwk.convention4j.common.json.jackson;

import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class PageQueryBean<C> {

    private int pageNumber;
    private int pageSize;

}
