package fun.fengwk.convention4j.common.validation;

import com.google.auto.service.AutoService;
import jakarta.validation.ConstraintValidator;

import java.util.Collection;
import java.util.Set;

/**
 * @author fengwk
 */
@AutoService(ConstraintValidator.class)
public class EnumNameCollectionValidator extends AbstractEnumNameValidator<Collection<? extends CharSequence>> {

    @Override
    protected boolean doIsValid(Collection<? extends CharSequence> collection, Set<String> enumNames) {
        if (collection != null && !collection.isEmpty()) {
            for (CharSequence cs : collection) {
                if (cs == null || !enumNames.contains(cs.toString())) {
                    return false;
                }
            }
        }
        return true;
    }

}
