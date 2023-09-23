package fun.fengwk.convention4j.api.page;

/**
 * 分页查询配置。
 * @author fengwk
 */
public class PageQueryConfig {

    /**
     * 最大的pageSize
     */
    private static volatile int MAX_PAGE_SIZE = 1000;

    public static void setMaxPageSize(int maxPageSize) {
        MAX_PAGE_SIZE = maxPageSize;
    }

    public static int getMaxPageSize() {
        return MAX_PAGE_SIZE;
    }

}
