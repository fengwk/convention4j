package fun.fengwk.convention4j.common.json.jackson;

import lombok.Data;

import java.util.List;

/**
 * @author fengwk
 */
@Data
public class PageBean<T> {

    private int pageNumber;
    private int pageSize;
    private List<T> results;
    private long totalCount;

}
