package fun.fengwk.convention4j.example.code;

import fun.fengwk.convention4j.common.code.ErrorCodes;

/**
 * @author fengwk
 */
public enum ExampleErrorCodes implements ErrorCodes {

    EXAMPLE_ERROR("0001")
    ;

    private static final String EXAMPLE = "Example";

    private final String value;

    ExampleErrorCodes(String value) {
        this.value = value;
    }

    @Override
    public String getDomain() {
        return EXAMPLE;
    }

    @Override
    public String getValue() {
        return value;
    }
}
