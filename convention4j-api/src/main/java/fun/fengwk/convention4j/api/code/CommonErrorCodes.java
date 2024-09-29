package fun.fengwk.convention4j.api.code;

import java.util.Collections;
import java.util.Map;

/**
 * 通用错误码。
 *
 * @author fengwk
 */
public enum CommonErrorCodes implements ConventionErrorCode {

    BAD_REQUEST(HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
    PAYMENT_REQUIRED(HttpStatus.PAYMENT_REQUIRED),
    FORBIDDEN(HttpStatus.FORBIDDEN),
    NOT_FOUND(HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED),
    NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE),
    PROXY_AUTHENTICATION_REQUIRED(HttpStatus.PROXY_AUTHENTICATION_REQUIRED),
    REQUEST_TIMEOUT(HttpStatus.REQUEST_TIMEOUT),
    CONFLICT(HttpStatus.CONFLICT),
    GONE(HttpStatus.GONE),
    LENGTH_REQUIRED(HttpStatus.LENGTH_REQUIRED),
    PRECONDITION_FAILED(HttpStatus.PRECONDITION_FAILED),
    PAYLOAD_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE),
    URI_TOO_LONG(HttpStatus.URI_TOO_LONG),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    REQUESTED_RANGE_NOT_SATISFIABLE(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE),
    EXPECTATION_FAILED(HttpStatus.EXPECTATION_FAILED),
    I_AM_A_TEAPOT(HttpStatus.I_AM_A_TEAPOT),
    UNPROCESSABLE_ENTITY(HttpStatus.UNPROCESSABLE_ENTITY),
    LOCKED(HttpStatus.LOCKED),
    FAILED_DEPENDENCY(HttpStatus.FAILED_DEPENDENCY),
    TOO_EARLY(HttpStatus.TOO_EARLY),
    UPGRADE_REQUIRED(HttpStatus.UPGRADE_REQUIRED),
    PRECONDITION_REQUIRED(HttpStatus.PRECONDITION_REQUIRED),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS),
    REQUEST_HEADER_FIELDS_TOO_LARGE(HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE),
    UNAVAILABLE_FOR_LEGAL_REASONS(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED),
    BAD_GATEWAY(HttpStatus.BAD_GATEWAY),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE),
    GATEWAY_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT),
    HTTP_VERSION_NOT_SUPPORTED(HttpStatus.HTTP_VERSION_NOT_SUPPORTED),
    VARIANT_ALSO_NEGOTIATES(HttpStatus.VARIANT_ALSO_NEGOTIATES),
    INSUFFICIENT_STORAGE(HttpStatus.INSUFFICIENT_STORAGE),
    LOOP_DETECTED(HttpStatus.LOOP_DETECTED),
    BANDWIDTH_LIMIT_EXCEEDED(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED),
    NOT_EXTENDED(HttpStatus.NOT_EXTENDED),
    NETWORK_AUTHENTICATION_REQUIRED(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED),
    ;

    private final HttpStatus httpStatus;

    CommonErrorCodes(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public int getStatus() {
        return httpStatus.getStatus();
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getMessage() {
        return httpStatus.getMessage();
    }

    @Override
    public Map<String, Object> getErrorContext() {
        return Collections.emptyMap();
    }

}
