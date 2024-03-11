package fun.fengwk.convention4j.common;

import fun.fengwk.convention4j.common.lang.CharSequenceView;
import org.junit.Test;

/**
 * @author fengwk
 */
public class CharSequenceViewTest {

    @Test
    public void test1() {
        assert new CharSequenceView("0123456789", 1, 5).toString().equals("1234");
    }

    @Test
    public void test2() {
        assert new CharSequenceView("0123456789", 0, 5).toString().equals("01234");
    }

    @Test
    public void test3() {
        assert new CharSequenceView("0123456789", 7, 10).toString().equals("789");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void test4() {
        new CharSequenceView("0123456789", 11, 10);
    }

    @Test
    public void test5() {
        assert new CharSequenceView("0123456789", 7, 10).subSequence(1, 2).toString().equals("8");
    }

    @Test
    public void test6() {
        assert new CharSequenceView("0123456789", 7, 10).subSequence(1, 3).toString().equals("89");
    }

    @Test
    public void test7() {
        assert new CharSequenceView("0123456789", 10, 10).subSequence(0, 0).toString().equals("");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void test8() {
        new CharSequenceView("0123456789", 0, 10).subSequence(1, 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void test9() {
        new CharSequenceView("0123456789", 0, 10).subSequence(1, 11);
    }

}
