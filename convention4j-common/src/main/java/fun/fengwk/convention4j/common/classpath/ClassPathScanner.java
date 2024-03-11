package fun.fengwk.convention4j.common.classpath;

import fun.fengwk.convention4j.common.lang.ClassUtils;
import fun.fengwk.convention4j.common.util.AntPattern;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * 类路径扫描器
 *
 * @author fengwk
 */
public class ClassPathScanner {
    
    public static final String CLASSPATH_SEPARATOR = "/";
    
    private static final String ROOT_CLASS_PATH = "";

    private static final String JAR = "jar";
    private static final String JAR_URL_PREFIX = "jar:";
    private static final String JAR_URL_SEPARATOR = "!/";

    private ClassLoader classLoader;

    public ClassPathScanner() {
        this(ClassUtils.getDefaultClassLoader());
    }
    
    public ClassPathScanner(ClassLoader cl) {
        this.classLoader = Objects.requireNonNull(cl);
    }
    
    /**
     * 扫描classpath中符合指定ANT模式的资源
     * 
     * @param antPattern not null，ant风格的路径匹配模式
     * @return
     * @throws IOException
     */
    public List<Resource> scan(String antPattern) throws IOException {
        if (antPattern == null) {
            throw new NullPointerException("antPattern cannot be null");
        }

        AntPattern pathMatcher = new AntPattern(antPattern);
        List<Resource> collector = new ArrayList<>();
        
        String rootPath = getRootPath(antPattern);
        Set<URL> rootUrls = getRootUrls(rootPath);
        
        for (URL rootUrl : rootUrls) {
            String protocol = rootUrl.getProtocol();
            ScanDelegate delegate = ScanDelegateFactory.getInstance(protocol);
            if (delegate == null) {
                throw new IllegalStateException("Cannot support protocol " + protocol);
            }
            
            delegate.scan(pathMatcher, collector, rootPath, rootUrl);
        }
        
        return collector;
    }
    
    private String getRootPath(String pattern) {
        // 如果模式中包含了通配符，就需要获取到通配符之前的路径，然后对带有通配符的路径进行遍历扫描
        int i1 = pattern.indexOf(AntPattern.ANY_SINGLE_CHARACTER);
        int i2 = pattern.indexOf(AntPattern.ANY_CHARACTER);

        if (i1 != -1 || i2 != -1) {
            int i;
            if (i1 != -1 && i2 != -1) {
                i = Math.min(i1, i2);
            } else if (i1 != -1) {// i1 != -1 && i2 == -1
                i = i1;
            } else {// i1 == -1 && i2 != -1
                i = i2;
            }
            
            pattern = pattern.substring(0, i);
            i = pattern.lastIndexOf(CLASSPATH_SEPARATOR);
            if (i != -1) {
                pattern = pattern.substring(0, i);
            } else {
                pattern = ROOT_CLASS_PATH;
            }
        }
        
        return pattern;
    }
    
    private Set<URL> getRootUrls(String rootPath) throws IOException {
        Set<URL> rootUrls = new LinkedHashSet<>();

        Enumeration<URL> urlEnum = classLoader.getResources(rootPath);
        while (urlEnum.hasMoreElements()) {
            URL url = urlEnum.nextElement();
            rootUrls.add(url);
        }
        
        if (ROOT_CLASS_PATH.equals(rootPath)) {
            ClassLoader cl = classLoader;
            while (cl != null) {
                if (cl instanceof URLClassLoader) {
                    for (URL url : ((URLClassLoader) cl).getURLs()) {
                        // 如果URL是这种形式：file:/C:/Program%20Files/idea/ideaIU-2021.1.2.win/plugins/junit/lib/junit5-rt.jar
                        // 将其转换为JAR形式：jar:file:/C:/Program%20Files/idea/ideaIU-2021.1.2.win/plugins/junit/lib/junit5-rt.jar!/
                        String urlStr = url.toString();
                        if (urlStr.endsWith(JAR)) {
                            url = new URL(JAR_URL_PREFIX + urlStr + JAR_URL_SEPARATOR);
                        }

                        rootUrls.add(url);
                    }
                }
                cl = cl.getParent();
            }
        }
        
        return rootUrls;
    }
    
}
