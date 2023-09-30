package fun.fengwk.convention4j.springboot.starter.validation;

import fun.fengwk.convention4j.common.validation.ConventionChecker;
import org.springframework.stereotype.Component;

/**
 * @author fengwk
 */
@Component
public class TeachChecker implements ConventionChecker<Teach> {

    @Override
    public void check(Teach teach) {
        if (teach == null) {
            throw new IllegalArgumentException("teach cannot be null");
        }
        if (teach.getId() == null) {
            throw new IllegalArgumentException("teach.id cannot be null");
        }
        if (teach.getName() == null) {
            throw new IllegalArgumentException("teach.name cannot be null");
        }
    }

}
