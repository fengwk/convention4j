package fun.fengwk.convention4j.common.validation;

/**
 * @author fengwk
 */
public enum ValidationMessageTemplate {

    ASSERT_FALSE("{javax.validation.constraints.AssertFalse.message}"),
    ASSERT_TRUE("{javax.validation.constraints.AssertTrue.message}"),
    DECIMAL_MAX("{javax.validation.constraints.DecimalMax.message}"),
    DECIMAL_MIN("{javax.validation.constraints.DecimalMin.message}"),
    DIGITS("{javax.validation.constraints.Digits.message}"),
    EMAIL("{javax.validation.constraints.Email.message}"),
    FUTURE("{javax.validation.constraints.Future.message}"),
    FUTURE_OR_PRESENT("{javax.validation.constraints.FutureOrPresent.message}"),
    MAX("{javax.validation.constraints.Max.message}"),
    MIN("{javax.validation.constraints.Min.message}"),
    NEGATIVE("{javax.validation.constraints.Negative.message}"),
    NEGATIVE_OR_ZERO("{javax.validation.constraints.NegativeOrZero.message}"),
    NOT_BLANK("{javax.validation.constraints.NotBlank.message}"),
    NOT_EMPTY("{javax.validation.constraints.NotEmpty.message}"),
    NOT_NULL("{javax.validation.constraints.NotNull.message}"),
    NULL("{javax.validation.constraints.Null.message}"),
    PAST("{javax.validation.constraints.Past.message}"),
    PAST_OR_PRESENT("{javax.validation.constraints.PastOrPresent.message}"),
    PATTERN("{javax.validation.constraints.Pattern.message}"),
    POSITIVE("{javax.validation.constraints.Positive.message}"),
    POSITIVE_OR_ZERO("{javax.validation.constraints.PositiveOrZero.message}"),
    SIZE("{javax.validation.constraints.Size.message}"),
    ;

    private final String template;

    ValidationMessageTemplate(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }

}
