package fun.fengwk.convention4j.common.path;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author fengwk
 */
@ToString
@EqualsAndHashCode
public class PathParser {

    private static final String DEFAULT_SEPARATOR = "/";
    private static final String DEFAULT_CURRENT_DIR = ".";
    private static final String DEFAULT_PARENT_DIR = "..";

    @Getter
    private final String separator;
    @Getter
    private final String currentDir;
    @Getter
    private final String parentDir;

    public PathParser() {
        this(DEFAULT_SEPARATOR, DEFAULT_CURRENT_DIR, DEFAULT_PARENT_DIR);
    }

    public PathParser(String separator, String currentDir, String parentDir) {
        Objects.requireNonNull(separator, "separator must not be null");
        Objects.requireNonNull(currentDir, "currentDir must not be null");
        Objects.requireNonNull(parentDir, "parentDir must not be null");
        this.separator = separator;
        this.currentDir = currentDir;
        this.parentDir = parentDir;
    }

    /**
     * 解析path为规范化路径
     *
     * @param path path
     * @return Path
     */
    public Path normalize(String path) {
        Objects.requireNonNull(path, "path must not be null");

        if (path.isEmpty()) {
            return new Path(".", Collections.emptyList(), false);
        }

        boolean absolute = path.startsWith(separator);
        String[] segments = path.split(Pattern.quote(separator));
        List<String> segmentList = new ArrayList<>();

        for (String segment : segments) {
            if (segment.isEmpty()) {
                continue;
            }
            if (Objects.equals(segment, currentDir)) {
                continue;
            }
            if (Objects.equals(segment, parentDir)) {
                if (!segmentList.isEmpty()) {
                    if (Objects.equals(segmentList.get(segmentList.size() - 1), parentDir)) {
                        // 处理相对路径中连续的".."
                        segmentList.add(segment);
                    } else {
                        segmentList.remove(segmentList.size() - 1);
                    }
                } else if (!absolute) {
                    segmentList.add(segment);
                }
                // 绝对路径中开头的".."会被忽略，符合Unix路径规则
            } else {
                segmentList.add(segment);
            }
        }

        if (segmentList.isEmpty()) {
            if (absolute) {
                return new Path(separator, Collections.emptyList(), true);
            } else {
                return new Path(".", Collections.emptyList(), false);
            }
        }

        String normalizedPath = String.join(separator, segmentList);
        if (absolute) {
            normalizedPath = separator + normalizedPath;
        }

        return new Path(normalizedPath, Collections.unmodifiableList(segmentList), absolute);
    }

}
