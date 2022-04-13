package fun.fengwk.convention4j.common.classpath;

import fun.fengwk.convention4j.common.AntPattern;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * 
 * @author fengwk
 */
abstract class ScanDelegate {

    abstract void scan(AntPattern antPattern, List<Resource> collector, String rootClasspath, URL rootUrl)
            throws IOException;

}
