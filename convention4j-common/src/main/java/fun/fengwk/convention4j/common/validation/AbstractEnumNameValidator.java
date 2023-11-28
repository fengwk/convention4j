package fun.fengwk.convention4j.common.validation;

import fun.fengwk.convention4j.api.validation.EnumName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.Set;

/**
 * @author fengwk
 */
public abstract class AbstractEnumNameValidator<T> implements ConstraintValidator<EnumName, T> {

    private volatile Set<String> enumNames;

    @Override
    public void initialize(EnumName enumNameAnno) {
        ConstraintValidator.super.initialize(enumNameAnno);

        Set<String> enumNames = new HashSet<>();
        Class<? extends Enum<?>> enumClass = enumNameAnno.value();
        Enum<?>[] enumConstants = enumClass.getEnumConstants();
        for (Enum<?> e : enumConstants) {
            enumNames.add(e.name());
        }
        this.enumNames = enumNames;
    }

    @Override
    public boolean isValid(T value, ConstraintValidatorContext context) {
        if (!doIsValid(value, enumNames)) {
            ValidationUtils.addMessageParameter(
                context, "value", "'" + String.join(",", enumNames) + "'");
            return false;
        }
        return true;
    }

    /**
     * 检查元素是否都与枚举名集合匹配
     */
    protected abstract boolean doIsValid(T value, Set<String> enumNames);

}
