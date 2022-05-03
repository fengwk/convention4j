package fun.fengwk.convention4j.common;

import org.junit.Test;

/**
 * @author fengwk
 */
public class StringUtilsTest {

    @Test
    public void testIsEmpty1() {
        assert StringUtils.isEmpty("");
    }

    @Test
    public void testIsEmpty2() {
        assert !StringUtils.isEmpty("123");
    }

    @Test
    public void testIsEmpty3() {
        assert StringUtils.isEmpty(null);
    }

    @Test
    public void testIsNotEmpty1() {
        assert !StringUtils.isNotEmpty("");
    }

    @Test
    public void testIsNotEmpty2() {
        assert StringUtils.isNotEmpty("123");
    }

    @Test
    public void testIsNotEmpty3() {
        assert !StringUtils.isNotEmpty(null);
    }

    @Test
    public void testIsBlank1() {
        assert StringUtils.isBlank("");
    }

    @Test
    public void testIsBlank2() {
        assert !StringUtils.isBlank("123");
    }

    @Test
    public void testIsBlank3() {
        assert StringUtils.isBlank(null);
    }

    @Test
    public void testIsBlank4() {
        assert StringUtils.isBlank("\t\n\r");
    }

    @Test
    public void testIsNotBlank1() {
        assert !StringUtils.isNotBlank("");
    }

    @Test
    public void testIsNotBlank2() {
        assert StringUtils.isNotBlank("123");
    }

    @Test
    public void testIsNotBlank3() {
        assert !StringUtils.isNotBlank(null);
    }

    @Test
    public void testIsNotBlank4() {
        assert !StringUtils.isNotBlank("\t\n\r");
    }

    @Test
    public void testEquals1() {
        assert StringUtils.equals("012345", 2, 6, "234567", 0, 4);
    }

    @Test
    public void testEquals2() {
        assert !StringUtils.equals("123", 2, 3, "12", 0, 2);
    }

    @Test
    public void testEquals3() {
        assert StringUtils.equals("012345", 2, "234567", 0, 3);
    }

}
