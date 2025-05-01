package fun.fengwk.convention4j.common.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

/**
 * @author fengwk
 */
public class ParametersBuilderTest {

    @Test
    public void test() {
        ParametersBuilder pb = new ParametersBuilder();
        pb.setParameter("name", "冯某某123a");
        pb.setParameter("age", "q w\"e");
        Assertions.assertEquals("name=%E5%86%AF%E6%9F%90%E6%9F%90123a&age=q%20w%22e", pb.build(StandardCharsets.UTF_8));
    }

}
