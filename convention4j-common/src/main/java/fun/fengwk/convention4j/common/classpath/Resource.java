package fun.fengwk.convention4j.common.classpath;

import fun.fengwk.convention4j.common.lang.StringUtils;

import java.net.URL;

/**
 * 
 * @author fengwk
 */
public class Resource {

    private final URL url;
    private final String classpath;
    
    public Resource(URL url, String classpath) {
        this.url = url;
        this.classpath = classpath;
    }

    public URL getURL() {
        return url;
    }
    
    public String getClasspath() {
        return classpath;
    }
    
    public String getName() {
        if (classpath.isEmpty()) {
            return StringUtils.EMPTY;
        }
        
        int i = classpath.lastIndexOf(ClassPathScanner.CLASSPATH_SEPARATOR);
        return classpath.substring(i + 1);
    }

    @Override
    public String toString() {
        return classpath;
    }
    
}
