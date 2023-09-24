package fun.fengwk.convention4j.common.validation;

import com.google.auto.service.AutoService;

import javax.validation.ConstraintValidator;
import java.util.Set;

/**
 * @author fengwk
 */
@AutoService(ConstraintValidator.class)
public class EnumNameValidator extends AbstractEnumNameValidator<CharSequence> {

    @Override
    protected boolean doIsValid(CharSequence cs, Set<String> enumNames) {
        return cs == null || enumNames.contains(cs.toString());
    }

}
