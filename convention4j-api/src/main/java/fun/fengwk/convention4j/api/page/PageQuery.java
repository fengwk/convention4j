package fun.fengwk.convention4j.api.page;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询器。
 *
 * @author fengwk
 */
@Data
public class PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private final int pageNumber;

    /**
     * 页面大小
     */
    private final int pageSize;

}
