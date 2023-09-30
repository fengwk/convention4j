package fun.fengwk.convention4j.common.validation;

import com.google.auto.service.AutoService;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author fengwk
 */
@AutoService(ConventionCheckerProvider.class)
public class ServiceLoaderConventionCheckerProvider implements ConventionCheckerProvider {

    @Override
    public String version() {
        return null;
    }

    @Override
    public VersionCheckerList getVersionCheckerList() {
        ServiceLoader<ConventionChecker> sl = ServiceLoader.load(ConventionChecker.class);
        List<ConventionChecker> checkers = new ArrayList<>();
        for (ConventionChecker checker : sl) {
            checkers.add(checker);
        }
        return new VersionCheckerList(null, checkers);
    }

}
