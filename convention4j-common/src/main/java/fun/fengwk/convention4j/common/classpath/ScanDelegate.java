package fun.fengwk.convention4j.common.classpath;

import fun.fengwk.convention4j.common.AntPattern;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * 类路径扫描委托器
 *
 * @author fengwk
 */
abstract class ScanDelegate {

    /**
     * 扫描类路径
     *
     * @param antPattern 路径模式
     * @param collector 资源收集器
     * @param rootClasspath 类的根路径
     * @param rootUrl 根路径对应的URL
     * @throws IOException
     */
    abstract void scan(AntPattern antPattern, List<Resource> collector, String rootClasspath, URL rootUrl)
            throws IOException;

}
