package fun.fengwk.convention4j.springboot.starter.webflux.context;

import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.nio.charset.StandardCharsets;

/**
 * @author fengwk
 */
public class RequestPathUtils {

    private RequestPathUtils() {}

    public static String extractPath(PathContainer pathContainer, String pattern) {
        PathPattern pathPattern = PathPatternParser.defaultInstance.parse(pattern);
        if (!pathPattern.matches(pathContainer)) {
            return "";
        }

        pathContainer = pathPattern.extractPathWithinPattern(pathContainer);
        String path = processPath(pathContainer.value());
        if (path.contains("%")) {
            path = org.springframework.util.StringUtils.uriDecode(path, StandardCharsets.UTF_8);
        }
        return path;
    }

    private static String processPath(String path) {
        boolean slash = false;
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == '/') {
                slash = true;
            }
            else if (path.charAt(i) > ' ' && path.charAt(i) != 127) {
                if (i == 0 || (i == 1 && slash)) {
                    return path;
                }
                path = slash ? "/" + path.substring(i) : path.substring(i);
                return path;
            }
        }
        return (slash ? "/" : "");
    }

}
