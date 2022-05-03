package fun.fengwk.convention4j.common.classpath;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * 
 * @author fengwk
 */
public class ClassPathScannerTest {

    @Test
    public void test() throws IOException {
        ClassPathScanner cps = new ClassPathScanner();
        List<Resource> resources = cps.scan("**/*.class");
        assert !resources.isEmpty();
    }
    
}
