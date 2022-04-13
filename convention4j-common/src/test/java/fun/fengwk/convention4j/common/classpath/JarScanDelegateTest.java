package fun.fengwk.convention4j.common.classpath;

import fun.fengwk.convention4j.common.AntPattern;
import org.junit.Test;

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
        URL rootUrl = FileScanDelegateTest.class.getClassLoader().getResource("junit");
        JarScanDelegate jsd = new JarScanDelegate();
        jsd.scan(pm, collector, "junit", rootUrl);
        boolean result = false;
        for (Resource resource : collector) {
            result |= resource.getURL().toString().endsWith(Test.class.getSimpleName() + ".class");
        }
        assert result;
    }
    
}
