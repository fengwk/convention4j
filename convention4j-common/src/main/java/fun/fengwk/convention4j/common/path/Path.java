package fun.fengwk.convention4j.common.path;

import lombok.Getter;

import java.util.List;
import java.util.Objects;

/**
 * @author fengwk
 */
public class Path {

    @Getter
    private final String path;
    @Getter
    private final List<String> segments;
    @Getter
    private final boolean absolute;

    Path(String path, List<String> segments, boolean absolute) {
        Objects.requireNonNull(path, "path must not be null");
        Objects.requireNonNull(segments, "segments must not be null");
        this.path = path;
        this.segments = segments;
        this.absolute = absolute;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        return Objects.equals(path, ((Path) o).path);
    }

    @Override
    public String toString() {
        return path;
    }

}
