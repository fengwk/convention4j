package fun.fengwk.convention4j.example.classpath;

import fun.fengwk.convention4j.common.classpath.ClassPathScanner;
import fun.fengwk.convention4j.common.classpath.Resource;

import java.io.IOException;
import java.util.List;

/**
 * @author fengwk
 */
public class ClassPathScannerExample {

    public static void main(String[] args) throws IOException {
        ClassPathScanner scanner = new ClassPathScanner(ClassPathScannerExample.class.getClassLoader());
        List<Resource> resources = scanner.scan("**/*.class");
        System.out.println(resources);
    }

}
