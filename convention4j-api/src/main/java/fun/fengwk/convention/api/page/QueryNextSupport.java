package fun.fengwk.convention.api.page;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * {@link QueryNextSupport}提供了一组方法用于获取查询是否具备下一页。
 * 
 * @author fengwk
 */
class QueryNextSupport {

    private QueryNextSupport() {}
    
    /**
     * 获取要查询的元素数量。
     * 
     * @param pageSize
     * @return
     */
    static long getLimit(int pageSize) {
        return pageSize + 1;
    }
    
    /**
     * 获取真实的结果集。
     * 
     * @param <E>
     * @param results
     * @param pageSize
     * @return
     */
    static <E> List<E> getRealResults(List<E> results, int pageSize) {
        List<E> realResult;
        if (results.size() == getLimit(pageSize)) {
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
     * @param resultsSize
     * @param pageSize
     * @return
     */
    static boolean hasNext(int resultsSize, int pageSize) {
        return resultsSize == getLimit(pageSize);
    }
    
}
