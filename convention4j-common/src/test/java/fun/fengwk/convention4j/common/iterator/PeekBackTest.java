package fun.fengwk.convention4j.common.iterator;

import org.junit.Test;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author fengwk
 */
public class PeekBackTest {

    @Test
    public void test1() {
        String str = "0123456789";
        Iterator<Character> charIterator = str.chars().mapToObj(c -> (char) c).iterator();
        PeekBackIterator<Character> iterator = Iterators.peekBack(Iterators.append(charIterator, Collections.singletonList('A')), Integer.MAX_VALUE);

        assert iterator.next() == '0';
        assert iterator.next() == '1';
        assert iterator.next() == '2';
        assert iterator.next() == '3';
        assert iterator.next() == '4';
        assert iterator.next() == '5';
        assert iterator.next() == '6';
        assert iterator.next() == '7';
        assert iterator.next() == '8';
        assert iterator.next() == '9';
        assert iterator.next() == 'A';

        iterator.putBack();
        iterator.putBack();
        iterator.putBack();
        iterator.putBack();
        iterator.putBack();
        iterator.putBack();
        iterator.putBack();
        iterator.putBack();
        iterator.putBack();
        iterator.putBack();
        iterator.putBack();

        assert iterator.next() == '0';
        assert iterator.next() == '1';
        assert iterator.next() == '2';
        assert iterator.next() == '3';
        assert iterator.next() == '4';
        assert iterator.next() == '5';
        assert iterator.next() == '6';
        assert iterator.next() == '7';
        assert iterator.next() == '8';
        assert iterator.next() == '9';
        assert iterator.next() == 'A';
    }

    @Test
    public void test2() {
        String str = "0123456789";
        Iterator<Character> charIterator = str.chars().mapToObj(c -> (char) c).iterator();
        PeekBackIterator<Character> iterator = Iterators.peekBack(Iterators.append(charIterator, Collections.singletonList('A')), 1);

        assert iterator.next() == '0';
        iterator.putBack();
        assert iterator.next() == '0';

        assert iterator.next() == '1';
        iterator.putBack();
        assert iterator.next() == '1';
    }

    @Test
    public void test3() {
        String str = "0123456789";
        Iterator<Character> charIterator = str.chars().mapToObj(c -> (char) c).iterator();
        PeekBackIterator<Character> iterator = Iterators.peekBack(Iterators.append(charIterator, Collections.singletonList('A')), 5);

        assert iterator.next() == '0';
        assert iterator.next() == '1';
        assert iterator.next() == '2';
        assert iterator.next() == '3';
        assert iterator.next() == '4';
        assert iterator.next() == '5';
        assert iterator.next() == '6';
        assert iterator.next() == '7';
        assert iterator.next() == '8';
        assert iterator.next() == '9';
        assert iterator.next() == 'A';

        iterator.putBack();
        iterator.putBack();
        iterator.putBack();
        iterator.putBack();
        iterator.putBack();

        assert iterator.next() == '6';
        assert iterator.next() == '7';
        assert iterator.next() == '8';
        assert iterator.next() == '9';
        assert iterator.next() == 'A';
    }

    @Test
    public void test4() {
        String str = "0123456789";
        Iterator<Character> charIterator = str.chars().mapToObj(c -> (char) c).iterator();
        PeekBackIterator<Character> iterator = Iterators.peekBack(Iterators.append(charIterator, Collections.singletonList('A')), Integer.MAX_VALUE);

        assert iterator.next() == '0';
        assert iterator.next() == '1';
        assert iterator.next() == '2';
        assert iterator.next() == '3';
        assert iterator.next() == '4';
        assert iterator.next(true) == '5';
        assert iterator.next(true) == '6';
        assert iterator.next(true) == '7';
        assert iterator.next(true) == '8';
        assert iterator.next(true) == '9';
        assert iterator.next(true) == 'A';

        iterator.putBack();
        iterator.putBack();
        iterator.putBack();
        iterator.putBack();
        iterator.putBack();

        assert iterator.next() == '0';
        assert iterator.next() == '1';
        assert iterator.next() == '2';
        assert iterator.next() == '3';
        assert iterator.next() == '4';
    }

    @Test(expected = NoSuchElementException.class)
    public void test5() {
        String str = "0123456789";
        Iterator<Character> charIterator = str.chars().mapToObj(c -> (char) c).iterator();
        PeekBackIterator<Character> iterator = Iterators.peekBack(charIterator, 3);

        assert iterator.next() == '0';
        assert iterator.next() == '1';
        assert iterator.next() == '2';
        assert iterator.next() == '3';

        iterator.putBack();
        iterator.putBack();
        iterator.putBack();
        iterator.putBack();
    }

    @Test(expected = NoSuchElementException.class)
    public void test6() {
        String str = "0123";
        Iterator<Character> charIterator = str.chars().mapToObj(c -> (char) c).iterator();
        PeekBackIterator<Character> iterator = Iterators.peekBack(charIterator, 3);

        assert iterator.next() == '0';
        assert iterator.next() == '1';
        assert iterator.next() == '2';
        assert iterator.next() == '3';
        iterator.next();
    }

}
