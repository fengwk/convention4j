package fun.fengwk.convention4j.common.classpath;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author fengwk
 */
class ScanDelegateFactory {

    private static final String PROTOCOL_FILE = "file";
    private static final String PROTOCOL_JAR = "jar";
    
    private static final Map<String, ScanDelegate> REGISTRY;
    
    static {
        Map<String, ScanDelegate> registry = new HashMap<>();
        registry.put(PROTOCOL_FILE, new FileScanDelegate());
        registry.put(PROTOCOL_JAR, new JarScanDelegate());
        REGISTRY = registry;
    }
    
    private ScanDelegateFactory() {}

    static ScanDelegate getInstance(String protocol) {
        return REGISTRY.get(protocol);
    }
    
}
