package fun.fengwk.convention4j.common.page;

/**
 * 分页查询限制器
 *
 * @author fengwk
 */
public class PageQueryLimiter {

    private static volatile int MAX_PAGE_SIZE = 1000;

    /**
     * 获取最大的分页大小
     *
     * @return
     */
    public static int getMaxPageSize() {
        return MAX_PAGE_SIZE;
    }

    /**
     * 修改最大的分页大小
     *
     * @param maxPageSize
     */
    public static void setMaxPageSize(int maxPageSize) {
        MAX_PAGE_SIZE = maxPageSize;
    }

}
