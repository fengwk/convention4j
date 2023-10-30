package fun.fengwk.convention4j.common.validation;

import com.google.auto.service.AutoService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fengwk
 */
@AutoService(ConventionChecker.class)
public class StudentNameChecker implements ConventionChecker<String> {

    @Override
    public void check(String name) {
        if (name.length() < 10) {
            Map<String, Object> messageParameters = new HashMap<>();
            messageParameters.put("min", 10);
            messageParameters.put("max", 100);
            throw new ValidationException("ff" + ValidationMessageTemplate.SIZE, messageParameters, "qwe");
        }
    }

}
