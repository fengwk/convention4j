package fun.fengwk.convention4j.springboot.starter.result;

import fun.fengwk.convention4j.api.code.ConventionErrorCode;
import fun.fengwk.convention4j.api.code.ErrorCode;

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
     * @param conventionErrorCode
     * @param ex
     * @return
     */
    public static ErrorCode toErrorCode(ConventionErrorCode conventionErrorCode, Throwable ex) {
        String msg = ex.getLocalizedMessage();
        if (msg == null) {
            msg = ex.getMessage();
        }
        ErrorCode finalErrorCode;
        if (msg == null || msg.trim().isEmpty()) {
            finalErrorCode = conventionErrorCode;
        } else {
            finalErrorCode = conventionErrorCode.create(msg);
        }
        return finalErrorCode;
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
