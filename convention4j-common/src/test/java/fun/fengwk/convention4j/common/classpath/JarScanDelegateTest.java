package fun.fengwk.convention4j.common.classpath;

import fun.fengwk.convention4j.common.util.AntPattern;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author fengwk
 */
public class JarScanDelegateTest {

    @Test
    public void test() throws IOException {
        AntPattern pm = new AntPattern("**");
        List<Resource> collector = new ArrayList<>();
        URL rootUrl = FileScanDelegateTest.class.getClassLoader().getResource("org/junit/jupiter/api");
        JarScanDelegate jsd = new JarScanDelegate();
        jsd.scan(pm, collector, "org/junit/jupiter/api", rootUrl);
        boolean result = false;
        for (Resource resource : collector) {
            result |= resource.getURL().toString().endsWith(Test.class.getSimpleName() + ".class");
        }
        assert result;
    }
    
}
