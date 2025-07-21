package fun.fengwk.convention4j.common.path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author fengwk
 */
public class PathParserTest {

    private final PathParser parser = new PathParser();

    @Test
    public void test() {
        Path p1 = parser.normalize("../a/../../b");
        Assertions.assertEquals("../../b", p1.getPath());
    }

    @Test
    public void testNormalizeEmptyPath() {
        Path path = parser.normalize("");
        assertFalse(path.isAbsolute());
        assertTrue(path.getSegments().isEmpty());
        assertEquals(".", path.getPath());
    }

    @Test
    public void testNormalizeRootPath() {
        Path path = parser.normalize("/");
        assertTrue(path.isAbsolute());
        assertTrue(path.getSegments().isEmpty());
        assertEquals("/", path.getPath());
    }

    @Test
    public void testNormalizeCurrentDir() {
        Path path = parser.normalize(".");
        assertFalse(path.isAbsolute());
        assertTrue(path.getSegments().isEmpty());
        assertEquals(".", path.getPath());
    }

    @Test
    public void testNormalizeRelativePath() {
        Path path = parser.normalize("a/b/c");
        assertFalse(path.isAbsolute());
        assertEquals(Arrays.asList("a", "b", "c"), path.getSegments());
        assertEquals("a/b/c", path.getPath());
    }

    @Test
    public void testNormalizeAbsolutePath() {
        Path path = parser.normalize("/a/b/c");
        assertTrue(path.isAbsolute());
        assertEquals(Arrays.asList("a", "b", "c"), path.getSegments());
        assertEquals("/a/b/c", path.getPath());
    }

    @Test
    public void testNormalizeWithDots() {
        Path path = parser.normalize("a/./b/./c");
        assertFalse(path.isAbsolute());
        assertEquals(Arrays.asList("a", "b", "c"), path.getSegments());
        assertEquals("a/b/c", path.getPath());
    }

    @Test
    public void testNormalizeWithParentDir() {
        Path path = parser.normalize("a/b/../c");
        assertFalse(path.isAbsolute());
        assertEquals(Arrays.asList("a", "c"), path.getSegments());
        assertEquals("a/c", path.getPath());
    }

    @Test
    public void testNormalizeWithMultipleParentDirs() {
        Path path = parser.normalize("a/b/c/../../..");
        assertFalse(path.isAbsolute());
        assertTrue(path.getSegments().isEmpty());
        assertEquals(".", path.getPath());
    }

    @Test
    public void testNormalizeAbsolutePathWithParentDirs() {
        Path path = parser.normalize("/a/b/c/../../..");
        assertTrue(path.isAbsolute());
        assertTrue(path.getSegments().isEmpty());
        assertEquals("/", path.getPath());
    }

    @Test
    public void testNormalizeWithUnresolvableParentDirs() {
        Path path = parser.normalize("../../a");
        assertFalse(path.isAbsolute());
        assertEquals(Arrays.asList("..", "..", "a"), path.getSegments());
        assertEquals("../../a", path.getPath());
    }

    @Test
    public void testNormalizeWithConsecutiveSeparators() {
        Path path = parser.normalize("a//b///c");
        assertFalse(path.isAbsolute());
        assertEquals(Arrays.asList("a", "b", "c"), path.getSegments());
        assertEquals("a/b/c", path.getPath());
    }

    @Test
    public void testNormalizeAbsolutePathWithConsecutiveSeparators() {
        Path path = parser.normalize("/a//b///c");
        assertTrue(path.isAbsolute());
        assertEquals(Arrays.asList("a", "b", "c"), path.getSegments());
        assertEquals("/a/b/c", path.getPath());
    }

    @Test
    public void testNormalizeWithLeadingAndTrailingSeparators() {
        Path path = parser.normalize("/a/b/c/");
        assertTrue(path.isAbsolute());
        assertEquals(Arrays.asList("a", "b", "c"), path.getSegments());
        assertEquals("/a/b/c", path.getPath());
    }

    @Test
    public void testNormalizeRelativePathWithLeadingDot() {
        Path path = parser.normalize("./a/b");
        assertFalse(path.isAbsolute());
        assertEquals(Arrays.asList("a", "b"), path.getSegments());
        assertEquals("a/b", path.getPath());
    }

    @Test
    public void testNormalizeAbsolutePathWithParentDirAtRoot() {
        Path path = parser.normalize("/../a");
        assertTrue(path.isAbsolute());
        assertEquals(Arrays.asList("a"), path.getSegments());
        assertEquals("/a", path.getPath());
    }

    @Test
    public void testNormalizeWithMixedDotsAndParentDirs() {
        Path path = parser.normalize("a/./../b/./c/../d");
        assertFalse(path.isAbsolute());
        assertEquals(Arrays.asList("b", "d"), path.getSegments());
        assertEquals("b/d", path.getPath());
    }

    @Test
    public void testNormalizeWithConsecutiveParentDirs() {
        Path path = parser.normalize("a/../../b");
        assertFalse(path.isAbsolute());
        assertEquals(Arrays.asList("..", "b"), path.getSegments());
        assertEquals("../b", path.getPath());
    }

    @Test
    public void testNormalizeAbsolutePathWithConsecutiveParentDirs() {
        Path path = parser.normalize("/a/../../b");
        assertTrue(path.isAbsolute());
        assertEquals(Arrays.asList("b"), path.getSegments());
        assertEquals("/b", path.getPath());
    }

    @Test
    public void testNormalizeWithAllDots() {
        Path path = parser.normalize(".....");
        assertFalse(path.isAbsolute());
        assertEquals(Arrays.asList("....."), path.getSegments());
        assertEquals(".....", path.getPath());
    }

    @Test
    public void testNormalizeWithSpecialCharacters() {
        Path path = parser.normalize("/a/b%20c/d@e/f#g");
        assertTrue(path.isAbsolute());
        assertEquals(Arrays.asList("a", "b%20c", "d@e", "f#g"), path.getSegments());
        assertEquals("/a/b%20c/d@e/f#g", path.getPath());
    }

}
