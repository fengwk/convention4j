package fun.fengwk.convention4j.common.validation;

import com.google.auto.service.AutoService;
import fun.fengwk.convention4j.api.validation.Checker;
import fun.fengwk.convention4j.common.lang.ClassUtils;
import fun.fengwk.convention4j.common.util.NullSafe;
import fun.fengwk.convention4j.common.util.OrderedObject;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
@AutoService(ConstraintValidator.class)
public class ConventionCheckerValidator implements ConstraintValidator<Checker, Object> {

    private static final List<ConventionCheckerFinder> FINDER_CHAIN;
    private volatile Checker checkerAnnotation;

    static {
        List<ConventionCheckerProvider> providerChain = new ArrayList<>();
        ServiceLoader<ConventionCheckerProvider> sl = ServiceLoader.load(
            ConventionCheckerProvider.class, ClassUtils.getDefaultClassLoader());
        for (ConventionCheckerProvider provider : sl) {
            providerChain.add(provider);
        }
        OrderedObject.sort(providerChain);
        FINDER_CHAIN = providerChain.stream()
            .map(ConventionCheckerFinder::new).collect(Collectors.toList());
    }

    @Override
    public void initialize(Checker checkerAnnotation) {
        ConstraintValidator.super.initialize(checkerAnnotation);
        this.checkerAnnotation = checkerAnnotation;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Class<?> valueClass = value.getClass();
        ConventionChecker<Object> checker = (ConventionChecker<Object>) getChecker(valueClass);
        if (checker == null) {
            if (checkerAnnotation.ignoreCheckerMissing()) {
                return true;
            } else {
                throw new IllegalStateException("ConventionChecker missing for '" + valueClass.getName() + "'");
            }
        }

        try {
            checker.check(value);
            return true;
        } catch (ValidationException ex) {
            context.disableDefaultConstraintViolation();
            Map<String, Object> messageParameters = NullSafe.of(ex.getMessageParameters());
            List<String> propertyNodes = NullSafe.of(ex.getPropertyNodes());
            for (Map.Entry<String, Object> entry : messageParameters.entrySet()) {
                ValidationUtils.addMessageParameter(context, entry.getKey(), entry.getValue());
            }
            ConstraintValidatorContext.ConstraintViolationBuilder builder = context.buildConstraintViolationWithTemplate(ex.getMessage());
            for (String propertyNode : propertyNodes) {
                builder.addPropertyNode(propertyNode).addBeanNode();
            }
            builder.addConstraintViolation();
        } catch (Throwable ex) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ex.getMessage())
                .addConstraintViolation();
        }
        return false;
    }

    private <T> ConventionChecker<T> getChecker(Class<T> valueClass) {
        for (ConventionCheckerFinder finder : FINDER_CHAIN) {
            ConventionChecker<T> checker = finder.getChecker(valueClass);
            if (checker != null) {
                return checker;
            }
        }
        return null;
    }

}
