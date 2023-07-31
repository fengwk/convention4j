package fun.fengwk.convention4j.common.page;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * {@link NextPageSupport}提供了一组方法用于获取发现下一页是否存在。
 * 
 * @author fengwk
 */
class NextPageSupport {

    private NextPageSupport() {}
    
    /**
     * 获取要查询的元素数量，pageSize+1。
     * 
     * @param pageSize 页面大小。
     * @return 要查询元素的数量。
     */
    static int getLimit(int pageSize) {
        return pageSize + 1;
    }

    /**
     * 获取真实的结果集。
     *
     * @param results 可能多查一个元素的结果集。
     * @param pageSize 页面大小。
     * @return 如果存在下一页，返回的结果集中将剔除最后一个元素。
     * @param <E> 元素类型。
     */
    static <E> List<E> getRealResults(List<E> results, int pageSize) {
        List<E> realResult;
        if (hasNext(results.size(), pageSize)) {
            realResult = new ArrayList<>();
            int c = results.size() - 1;
            Iterator<E> iterator = results.iterator();
            while (c > 0) {
                realResult.add(iterator.next());
                c--;
            }
        } else {
            realResult = new ArrayList<>(results);
        }
        return realResult;
    }

    /**
     * 判断是否存在下一页。
     * 
     * @param resultsSize 结果集大小。
     * @param pageSize 页面大小。
     * @return true-存在下一页，false-不存在下一页。
     */
    static boolean hasNext(int resultsSize, int pageSize) {
        return resultsSize == getLimit(pageSize);
    }
    
}
