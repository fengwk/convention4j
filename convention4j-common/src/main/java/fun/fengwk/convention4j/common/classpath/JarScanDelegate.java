package fun.fengwk.convention4j.common.classpath;

import fun.fengwk.convention4j.common.util.AntPattern;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 
 * @author fengwk
 */
class JarScanDelegate extends ScanDelegate {
    
    private static final String JAR_URL_SEPARATOR = "!/";
    
    @Override
    void scan(AntPattern antPattern, List<Resource> collector, String currentClasspath, URL currentUrl) throws IOException {
        JarURLConnection conn = (JarURLConnection) currentUrl.openConnection();
        if (conn != null) {
            conn.setUseCaches(false);
            try (JarFile jarFile = conn.getJarFile()) {
                Enumeration<JarEntry> jarEntries = jarFile.entries();
                while (jarEntries.hasMoreElements()) {
                    JarEntry jarEntry = jarEntries.nextElement();
                    String entryPath = jarEntry.getName();
                    if (antPattern.match(entryPath)) {
                        collector.add(new Resource(toURL(currentUrl, jarEntry.getName()), entryPath));
                    }
                }
            }
        }
    }
    
    private URL toURL(URL baseUrl, String entryName) throws MalformedURLException {
        String u = baseUrl.toString();
        int i = u.indexOf(JAR_URL_SEPARATOR);
        if (i != -1) {
            u = u.substring(0, i);
        }
        return new URL(u + JAR_URL_SEPARATOR + entryName);
    }

}
