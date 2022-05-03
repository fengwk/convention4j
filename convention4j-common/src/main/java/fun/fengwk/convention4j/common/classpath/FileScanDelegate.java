package fun.fengwk.convention4j.common.classpath;

import fun.fengwk.convention4j.common.AntPattern;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/**
 * 
 * @author fengwk
 */
class FileScanDelegate extends ScanDelegate {
    
    @Override
    void scan(AntPattern antPattern, List<Resource> collector, String rootClasspath, URL rootUrl) throws IOException {
        File rootFile = toFile(rootUrl);
        doScan(antPattern, collector, rootClasspath, rootFile);
    }
    
    void doScan(AntPattern antPattern, List<Resource> collector, String currentClasspath, File currentFile) throws IOException {
        if (antPattern.match(currentClasspath)) {
            collector.add(new Resource(toURL(currentFile), currentClasspath));
        }
        
        if (currentFile.isDirectory()) {
            File[] children = currentFile.listFiles();
            if (children != null) {
                for (File child : children) {
                    doScan(antPattern, collector, appendPath(currentClasspath, child.getName()), child);
                }
            }
        }
    }
    
    private String appendPath(String path, String name) {
        if (path.isEmpty()) {
            return name;
        } else {
            return path + AntPattern.SEPARATOR + name;
        }
    }
    
    private File toFile(URL url) throws IOException {
        try {
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }
    
    private URL toURL(File file) throws MalformedURLException {
        return file.toURI().toURL();
    }

}
