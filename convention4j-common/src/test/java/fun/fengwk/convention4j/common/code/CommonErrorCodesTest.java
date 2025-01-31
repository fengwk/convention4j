package fun.fengwk.convention4j.common.code;

import fun.fengwk.convention4j.api.code.CommonErrorCodes;
import fun.fengwk.convention4j.api.code.ConventionErrorCode;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.result.Results;
import fun.fengwk.convention4j.common.util.MapUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author fengwk
 */
public class CommonErrorCodesTest {

    @Test
    public void testWithErrorContext() {
        ConventionErrorCode errorCode = CommonErrorCodes.BAD_REQUEST.withErrorContext(
            MapUtils.newMap("age", 18));
        System.out.println(errorCode);

        Result<Object> result = Results.error(errorCode);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getErrors());
        System.out.println(result);
    }

}
