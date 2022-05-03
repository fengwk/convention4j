package fun.fengwk.convention4j.springboot.starter.result;

import fun.fengwk.convention4j.common.code.CodeTable;
import fun.fengwk.convention4j.common.code.ErrorCode;
import fun.fengwk.convention4j.common.code.ErrorCodeFactory;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fengwk
 */
public class ExceptionHandlerUtils {

    private ExceptionHandlerUtils() {}

    /**
     * 将异常转化为指定类型的错误码。
     *
     * @param errorCodeFactory
     * @param errCode
     * @param ex
     * @return
     */
    public static ErrorCode toErrorCode(ErrorCodeFactory errorCodeFactory, CodeTable errCode, Throwable ex) {
        String msg = ex.getLocalizedMessage();
        ErrorCode errorCode;
        if (msg == null || msg.trim().isEmpty()) {
            errorCode = errorCodeFactory.create(errCode);
        } else {
            errorCode = errorCodeFactory.create(errCode, msg);
        }
        return errorCode;
    }

    /**
     * 从ConstraintViolationException中提取错误信息映射。
     *
     * @param ex
     * @return
     */
    public static Map<String, String> convertToErrors(ConstraintViolationException ex) {
        Map<String, String> map = new HashMap<>();
        if (ex.getConstraintViolations() != null) {
            for (ConstraintViolation<?> cv : ex.getConstraintViolations()) {
                map.put(getProperty(cv.getPropertyPath()), cv.getMessage());
            }
        }
        return map;
    }

    private static String getProperty(Path path) {
        StringBuilder property = new StringBuilder();
        for (Path.Node node : path) {
            ElementKind kind = node.getKind();
            if (kind == ElementKind.PARAMETER || kind == ElementKind.PROPERTY || kind == ElementKind.RETURN_VALUE) {
                if (property.length() > 0) {
                    property.append('.');
                }
                property.append(node.getName());
            }
        }
        return property.length() > 0 ? property.toString() : path.toString();
    }

}
