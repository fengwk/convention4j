package fun.fengwk.convention4j.common.code;


import fun.fengwk.convention4j.api.code.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用错误码
 *
 * @author fengwk
 */
@Getter
@AllArgsConstructor
public enum ErrorCodes implements ConventionErrorCodeFactory {

    BAD_REQUEST(400, HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(401, HttpStatus.UNAUTHORIZED),
    PAYMENT_REQUIRED(402, HttpStatus.PAYMENT_REQUIRED),
    FORBIDDEN(403, HttpStatus.FORBIDDEN),
    NOT_FOUND(404, HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED(405, HttpStatus.METHOD_NOT_ALLOWED),
    NOT_ACCEPTABLE(406, HttpStatus.NOT_ACCEPTABLE),
    PROXY_AUTHENTICATION_REQUIRED(407, HttpStatus.PROXY_AUTHENTICATION_REQUIRED),
    REQUEST_TIMEOUT(408, HttpStatus.REQUEST_TIMEOUT),
    CONFLICT(409, HttpStatus.CONFLICT),
    GONE(410, HttpStatus.GONE),
    LENGTH_REQUIRED(411, HttpStatus.LENGTH_REQUIRED),
    PRECONDITION_FAILED(412, HttpStatus.PRECONDITION_FAILED),
    PAYLOAD_TOO_LARGE(413, HttpStatus.PAYLOAD_TOO_LARGE),
    URI_TOO_LONG(414, HttpStatus.URI_TOO_LONG),
    UNSUPPORTED_MEDIA_TYPE(415, HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    REQUESTED_RANGE_NOT_SATISFIABLE(416, HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE),
    EXPECTATION_FAILED(417, HttpStatus.EXPECTATION_FAILED),
    I_AM_A_TEAPOT(418, HttpStatus.I_AM_A_TEAPOT),
    UNPROCESSABLE_ENTITY(422, HttpStatus.UNPROCESSABLE_ENTITY),
    LOCKED(423, HttpStatus.LOCKED),
    FAILED_DEPENDENCY(424, HttpStatus.FAILED_DEPENDENCY),
    TOO_EARLY(425, HttpStatus.TOO_EARLY),
    UPGRADE_REQUIRED(426, HttpStatus.UPGRADE_REQUIRED),
    PRECONDITION_REQUIRED(428, HttpStatus.PRECONDITION_REQUIRED),
    TOO_MANY_REQUESTS(429, HttpStatus.TOO_MANY_REQUESTS),
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE),
    UNAVAILABLE_FOR_LEGAL_REASONS(451, HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS),

    INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_IMPLEMENTED(501, HttpStatus.NOT_IMPLEMENTED),
    BAD_GATEWAY(502, HttpStatus.BAD_GATEWAY),
    SERVICE_UNAVAILABLE(503, HttpStatus.SERVICE_UNAVAILABLE),
    GATEWAY_TIMEOUT(504, HttpStatus.GATEWAY_TIMEOUT),
    HTTP_VERSION_NOT_SUPPORTED(505, HttpStatus.HTTP_VERSION_NOT_SUPPORTED),
    VARIANT_ALSO_NEGOTIATES(506, HttpStatus.VARIANT_ALSO_NEGOTIATES),
    INSUFFICIENT_STORAGE(507, HttpStatus.INSUFFICIENT_STORAGE),
    LOOP_DETECTED(508, HttpStatus.LOOP_DETECTED),
    BANDWIDTH_LIMIT_EXCEEDED(509, HttpStatus.BANDWIDTH_LIMIT_EXCEEDED),
    NOT_EXTENDED(510, HttpStatus.NOT_EXTENDED),
    NETWORK_AUTHENTICATION_REQUIRED(511, HttpStatus.NETWORK_AUTHENTICATION_REQUIRED),
    ;

    private final int domainCode;
    private final HttpStatus httpStatus;

    @Override
    public String getDomain() {
        return "C";
    }

    public static ErrorCodes of(int domainCode) {
        for (ErrorCodes errorCode : ErrorCodes.values()) {
            if (errorCode.getDomainCode() == domainCode) {
                return errorCode;
            }
        }
        return null;
    }

    public static ErrorCodes of(HttpStatus httpStatus) {
        for (ErrorCodes errorCode : ErrorCodes.values()) {
            if (errorCode.getHttpStatus() == httpStatus) {
                return errorCode;
            }
        }
        return null;
    }

}
