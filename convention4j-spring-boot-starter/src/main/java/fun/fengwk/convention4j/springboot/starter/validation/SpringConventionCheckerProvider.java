package fun.fengwk.convention4j.springboot.starter.validation;

import com.google.auto.service.AutoService;
import fun.fengwk.convention4j.common.validation.ConventionChecker;
import fun.fengwk.convention4j.common.validation.ConventionCheckerProvider;
import fun.fengwk.convention4j.common.validation.VersionCheckerList;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author fengwk
 */
@AutoService(ConventionCheckerProvider.class)
public class SpringConventionCheckerProvider implements ConventionCheckerProvider {

    private static volatile ApplicationContext applicationContext;

    static void setBeanFactory(ApplicationContext applicationContext) {
        SpringConventionCheckerProvider.applicationContext = applicationContext;
    }

    @Override
    public String version() {
        if (applicationContext == null) {
            return null;
        }
        if (applicationContext.getId() != null) {
            return applicationContext.getId();
        }
        return String.valueOf(applicationContext.hashCode());
    }

    @Override
    public VersionCheckerList getVersionCheckerList() {
        if (applicationContext == null) {
            return new VersionCheckerList(null, Collections.emptyList());
        }
        ObjectProvider<ConventionChecker> provider = applicationContext.getBeanProvider(ConventionChecker.class);
        List<ConventionChecker> checkers = new ArrayList<>();
        for (ConventionChecker checker : provider) {
            checkers.add(checker);
        }
        return new VersionCheckerList(applicationContext.getId(), checkers);
    }

}
