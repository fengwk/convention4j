package fun.fengwk.convention4j.example.code;

import fun.fengwk.convention4j.common.code.CodeTable;
import fun.fengwk.convention4j.common.code.ErrorCode;

/**
 * @author fengwk
 */
public enum ExampleCodeTable implements CodeTable {

    EXAMPLE_ERROR(encodeCode("0001"))

    ;

    private static final String EXAMPLE = "Example";

    private final String code;

    ExampleCodeTable(String code) {
        this.code = code;
    }

    static String encodeCode(String num) {
        return ErrorCode.encodeCode(EXAMPLE, num);
    }

    @Override
    public String getCode() {
        return code;
    }
}
