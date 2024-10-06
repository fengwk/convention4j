package fun.fengwk.convention4j.common.iterator;

import fun.fengwk.convention4j.common.util.Order;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
public class CursorUnionTest {

    private List<Integer> queryAsc(List<Integer> db, Integer cursor, int limit) {
        if (cursor == null) {
            return db.stream().limit(limit).collect(Collectors.toList());
        } else {
            return db.stream().filter(i -> i == null || i > cursor).limit(limit).collect(Collectors.toList());
        }
    }

    private List<Integer> queryDesc(List<Integer> db, Integer cursor, int limit) {
        if (cursor == null) {
            return db.stream().limit(limit).collect(Collectors.toList());
        } else {
            return db.stream().filter(i -> i == null || i < cursor).limit(limit).collect(Collectors.toList());
        }
    }

    @Test
    public void test1() {
        List<Integer> db1 = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            db1.add(i);
        }
        CursorIterator<Integer, Integer> iter1 = Iterators.cursor(
                (CursorQueryFunction<Integer, Integer>) (cursor, limit) -> queryAsc(db1, cursor, limit),
                Function.identity(),
                Order.ASC,
                1);

        List<Integer> db2 = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            db2.add(i);
        }
        CursorIterator<Integer, Integer> iter2 = Iterators.cursor(
                (CursorQueryFunction<Integer, Integer>) (cursor, limit) -> queryAsc(db2, cursor, limit),
                Function.identity(),
                Order.ASC,
                1);

        Iterator<Integer> unionIter = Iterators.cursorUnion(
                Arrays.asList(iter1, iter2),
                i -> i - 1);
        List<Integer> result = new ArrayList<>();
        while (unionIter.hasNext()) {
            result.add(unionIter.next());
        }
        assert db1.equals(result);
    }

    @Test
    public void test2() {
        List<Integer> db1 = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            db1.add(i);
        }
        CursorIterator<Integer, Integer> iter1 = Iterators.cursor(
                (CursorQueryFunction<Integer, Integer>) (cursor, limit) -> queryAsc(db1, cursor, limit),
                Function.identity(),
                Order.ASC,
                1);

        List<Integer> db2 = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            if (i % 2 == 0) {
                db2.add(i);
            }
        }
        CursorIterator<Integer, Integer> iter2 = Iterators.cursor(
                (CursorQueryFunction<Integer, Integer>) (cursor, limit) -> queryAsc(db2, cursor, limit),
                Function.identity(),
                Order.ASC,
                1);

        Iterator<Integer> unionIter = Iterators.cursorUnion(
                Arrays.asList(iter1, iter2),
                i -> i - 1);
        List<Integer> result = new ArrayList<>();
        while (unionIter.hasNext()) {
            result.add(unionIter.next());
        }
        assert db2.equals(result);
    }

    @Test
    public void test3() {
        List<Integer> db1 = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            db1.add(i);
        }
        CursorIterator<Integer, Integer> iter1 = Iterators.cursor(
                (CursorQueryFunction<Integer, Integer>) (cursor, limit) -> queryAsc(db1, cursor, limit),
                Function.identity(),
                Order.ASC,
                1);

        List<Integer> db2 = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            if (i % 2 == 0) {
                db2.add(i);
            }
        }
        CursorIterator<Integer, Integer> iter2 = Iterators.cursor(
                (CursorQueryFunction<Integer, Integer>) (cursor, limit) -> queryAsc(db2, cursor, limit),
                Function.identity(),
                Order.ASC,
                1);

        Iterator<Integer> unionIter = Iterators.cursorUnion(
                Arrays.asList(iter1, iter2),
                i -> i - 1);
        List<Integer> result = new ArrayList<>();
        while (unionIter.hasNext()) {
            result.add(unionIter.next());
        }
        assert db2.equals(result);
    }

    @Test
    public void test4() {
        List<Integer> db1 = new ArrayList<>();
        for (int i = 29; i >= 0; i--) {
            db1.add(i);
        }
        CursorIterator<Integer, Integer> iter1 = Iterators.cursor(
                (CursorQueryFunction<Integer, Integer>) (cursor, limit) -> queryDesc(db1, cursor, limit),
                Function.identity(),
                Order.DESC,
                1);

        List<Integer> db2 = new ArrayList<>();
        for (int i = 29; i >= 0; i--) {
            db2.add(i);
        }
        CursorIterator<Integer, Integer> iter2 = Iterators.cursor(
                (CursorQueryFunction<Integer, Integer>) (cursor, limit) -> queryDesc(db2, cursor, limit),
                Function.identity(),
                Order.DESC,
                1);

        Iterator<Integer> unionIter = Iterators.cursorUnion(
                Arrays.asList(iter1, iter2),
                i -> i + 1);
        List<Integer> result = new ArrayList<>();
        while (unionIter.hasNext()) {
            result.add(unionIter.next());
        }
        assert db1.equals(result);
    }

    @Test
    public void test5() {
        List<Integer> db1 = new ArrayList<>();
        for (int i = 29; i >= 0; i--) {
            db1.add(i);
        }
        CursorIterator<Integer, Integer> iter1 = Iterators.cursor(
                (CursorQueryFunction<Integer, Integer>) (cursor, limit) -> queryDesc(db1, cursor, limit),
                Function.identity(),
                Order.DESC,
                1);

        List<Integer> db2 = new ArrayList<>();
        for (int i = 29; i >= 0; i--) {
            if (i % 2 == 0) {
                db2.add(i);
            }
        }
        CursorIterator<Integer, Integer> iter2 = Iterators.cursor(
                (CursorQueryFunction<Integer, Integer>) (cursor, limit) -> queryDesc(db2, cursor, limit),
                Function.identity(),
                Order.DESC,
                1);

        Iterator<Integer> unionIter = Iterators.cursorUnion(
                Arrays.asList(iter1, iter2),
                i -> i + 1);
        List<Integer> result = new ArrayList<>();
        while (unionIter.hasNext()) {
            result.add(unionIter.next());
        }
        assert db2.equals(result);
    }

}
