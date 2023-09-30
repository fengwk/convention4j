package fun.fengwk.convention4j.common.validation;

import java.util.List;

/**
 * @author fengwk
 */
public class VersionCheckerList {

    private final String version;
    private final List<ConventionChecker> checkers;

    public VersionCheckerList(String version, List<ConventionChecker> checkers) {
        this.version = version;
        this.checkers = checkers;
    }

    public String getVersion() {
        return version;
    }

    public List<ConventionChecker> getCheckers() {
        return checkers;
    }

}
