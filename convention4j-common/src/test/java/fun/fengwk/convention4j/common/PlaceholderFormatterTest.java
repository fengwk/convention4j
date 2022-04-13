package fun.fengwk.convention4j.common;

import org.junit.Test;

/**
 * 
 * @author fengwk
 */
public class PlaceholderFormatterTest {

    @Test
    public void test() {
        PlaceholderFormatter pf = new PlaceholderFormatter("{}");
        String out = pf.format("hello, {}", "fengwk");
        assert out.equals("hello, fengwk");
    }
    
}
