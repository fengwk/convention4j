package fun.fengwk.convention4j.common.validation;

/**
 * @author fengwk
 */
public class ValidationMessageTemplate {

    private ValidationMessageTemplate() {}

    public static final String ASSERT_FALSE = "{javax.validation.constraints.AssertFalse.message}";
    public static final String ASSERT_TRUE = "{javax.validation.constraints.AssertTrue.message}";
    public static final String DECIMAL_MAX = "{javax.validation.constraints.DecimalMax.message}";
    public static final String DECIMAL_MIN = "{javax.validation.constraints.DecimalMin.message}";
    public static final String DIGITS = "{javax.validation.constraints.Digits.message}";
    public static final String EMAIL = "{javax.validation.constraints.Email.message}";
    public static final String FUTURE = "{javax.validation.constraints.Future.message}";
    public static final String FUTURE_OR_PRESENT = "{javax.validation.constraints.FutureOrPresent.message}";
    public static final String MAX = "{javax.validation.constraints.Max.message}";
    public static final String MIN = "{javax.validation.constraints.Min.message}";
    public static final String NEGATIVE = "{javax.validation.constraints.Negative.message}";
    public static final String NEGATIVE_OR_ZERO = "{javax.validation.constraints.NegativeOrZero.message}";
    public static final String NOT_BLANK = "{javax.validation.constraints.NotBlank.message}";
    public static final String NOT_EMPTY = "{javax.validation.constraints.NotEmpty.message}";
    public static final String NOT_NULL = "{javax.validation.constraints.NotNull.message}";
    public static final String NULL = "{javax.validation.constraints.Null.message}";
    public static final String PAST = "{javax.validation.constraints.Past.message}";
    public static final String PAST_OR_PRESENT = "{javax.validation.constraints.PastOrPresent.message}";
    public static final String PATTERN = "{javax.validation.constraints.Pattern.message}";
    public static final String POSITIVE = "{javax.validation.constraints.Positive.message}";
    public static final String POSITIVE_OR_ZERO = "{javax.validation.constraints.PositiveOrZero.message}";
    public static final String SIZE = "{javax.validation.constraints.Size.message}";

}
