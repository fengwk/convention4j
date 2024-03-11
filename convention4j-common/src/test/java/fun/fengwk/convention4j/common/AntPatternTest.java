package fun.fengwk.convention4j.common;

import fun.fengwk.convention4j.common.util.AntPattern;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author fengwk
 */
public class AntPatternTest {

    @Test
    public void testSplit1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String[] segments = executeSplit("/a/b/c");
        assert segments.length == 3;
        assert segments[0].equals("a");
        assert segments[1].equals("b");
        assert segments[2].equals("c");
    }

    @Test
    public void testSplit2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String[] segments = executeSplit("/a/b/c//");
        assert segments.length == 3;
        assert segments[0].equals("a");
        assert segments[1].equals("b");
        assert segments[2].equals("c");
    }

    @Test
    public void testSplit3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String[] segments = executeSplit("/abc////bbb/c//");
        assert segments.length == 3;
        assert segments[0].equals("abc");
        assert segments[1].equals("bbb");
        assert segments[2].equals("c");
    }

    @Test
    public void testSplit4() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String[] segments = executeSplit("abc////**/?c//");
        assert segments.length == 3;
        assert segments[0].equals("abc");
        assert segments[1].equals("**");
        assert segments[2].equals("?c");
    }

    @Test
    public void testSplit5() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String[] segments = executeSplit("///");
        assert segments.length == 0;
    }

    @Test
    public void testSplit6() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String[] segments = executeSplit("/");
        assert segments.length == 0;
    }

    @Test
    public void testSplit7() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String[] segments = executeSplit("");
        assert segments.length == 0;
    }

    private String[] executeSplit(String path) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        AntPattern antPath = new AntPattern("any string");
        Method splitMethod = AntPattern.class.getDeclaredMethod("split", String.class);
        splitMethod.setAccessible(true);
        return (String[]) splitMethod.invoke(antPath, path);
    }

    @Test
    public void testMatch() {
        assert !new AntPattern("").match(null);
        assert new AntPattern("").match("");
        assert new AntPattern("/a/*").match("/a/b");
        assert !new AntPattern("/a/*").match("/a/b/c");
        assert new AntPattern("/a/??").match("/a/ab");
        assert new AntPattern("/a/**").match("/a/b/c");
        assert !new AntPattern("a/**").match("/a/b/c");
        assert new AntPattern("**/a/**").match("a/b/c");
        assert new AntPattern("**/a?**?c/**").match("aasc/b/c");
    }

}
