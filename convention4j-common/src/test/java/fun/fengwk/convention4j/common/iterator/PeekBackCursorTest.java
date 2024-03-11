package fun.fengwk.convention4j.common.iterator;

import fun.fengwk.convention4j.common.util.Order;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
public class PeekBackCursorTest {

    private List<Integer> query(List<Integer> db, Integer cursor, int limit) {
        if (cursor == null) {
            return db.stream().limit(limit).collect(Collectors.toList());
        } else {
            return db.stream().filter(i -> i == null || i > cursor).limit(limit).collect(Collectors.toList());
        }
    }

    @Test
    public void test1() {
        List<Integer> db = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            db.add(i);
        }
        CursorIterator<Integer, Integer> iter = Iterators.peekBack(Iterators.cursor(
                (CursorQueryFunction<Integer, Integer>) (cursor, limit) -> query(db, cursor, limit),
                Function.identity(),
                Order.ASC,
                1,
                null), 1);
        List<Integer> result = new ArrayList<>();
        while (iter.hasNext()) {
            result.add(iter.next());
        }
        assert db.equals(result);
    }

    @Test
    public void test2() {
        List<Integer> db = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            db.add(i);
        }
        CursorIterator<Integer, Integer> iter = Iterators.peekBack(Iterators.cursor(
                (CursorQueryFunction<Integer, Integer>) (cursor, limit) -> query(db, cursor, limit),
                Function.identity(),
                Order.ASC,
                1,
                null), 1);

        assert iter.getCursor() == null;
        assert iter.next() == 0;

        assert iter.getCursor() == 0;
        assert iter.next() == 1;

        assert iter.getCursor() == 1;
        assert iter.next() == 2;

        assert iter.getCursor() == 2;
        assert iter.next() == 3;

        assert iter.getCursor() == 3;
        assert iter.next() == 4;

        assert !iter.hasNext();
    }

}
