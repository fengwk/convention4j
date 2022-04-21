package fun.fengwk.convention4j.common;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author fengwk
 */
public class ListOpsTest {

    @Test
    public void testGetFirst() {
        List<Integer> list = asList(1, 2, 3);
        assert ListUtils.getFirst(list) == 1;
    }
    
    @Test
    public void testGetLast() {
        List<Integer> list = asList(1, 2, 3);
        assert ListUtils.getLast(list) == 3;
    }
    
    @Test
    public void testAddFirst() {
        List<Integer> list = asList(1, 2, 3);
        ListUtils.addFirst(list, 0);
        assert ListUtils.getFirst(list) == 0;
    }
    
    @Test
    public void testAddLast() {
        List<Integer> list = asList(1, 2, 3);
        ListUtils.addLast(list, 4);
        assert ListUtils.getLast(list) == 4;
    }
    
    @Test
    public void testSetFirst() {
        List<Integer> list = asList(1, 2, 3);
        assert ListUtils.setFirst(list, 0) == 1;
        assert ListUtils.getFirst(list) == 0;
    }
    
    @Test
    public void testSetLast() {
        List<Integer> list = asList(1, 2, 3);
        assert ListUtils.setLast(list, 4) == 3;
        assert ListUtils.getLast(list) == 4;
    }
    
    @Test
    public void testRemoveFirst() {
        List<Integer> list = asList(1, 2, 3);
        assert ListUtils.removeFirst(list) == 1;
    }
    
    @Test
    public void testRemoveLast() {
        List<Integer> list = asList(1, 2, 3);
        assert ListUtils.removeLast(list) == 3;
    }

    @SafeVarargs
    private final <T> List<T> asList(T... arr) {
        return new ArrayList<>(Arrays.asList(arr));
    }
    
}
