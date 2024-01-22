package fun.fengwk.convention4j.oauth2.share.constant;

import fun.fengwk.convention4j.api.code.ConventionErrorCode;
import fun.fengwk.convention4j.api.code.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * OAuth4j错误码
 * @author fengwk
 */
@Getter
@AllArgsConstructor
public enum OAuth2ErrorCodes implements ConventionErrorCode {

    CLIENT_NOT_FOUND(1001, HttpStatus.BAD_REQUEST),
    INVALID_CLIENT_SECRET(1002, HttpStatus.UNAUTHORIZED),
    CLIENT_ALREADY_EXISTS(1003, HttpStatus.BAD_REQUEST),

    INVALID_GRANT_TYPE(1004, HttpStatus.BAD_REQUEST),
    UNKNOWN_GRANT_TYPE(1005, HttpStatus.BAD_REQUEST),
    UNSUPPORTED_GRANT_TYPE(1006, HttpStatus.BAD_REQUEST),

    INVALID_RESPONSE_TYPE(1007, HttpStatus.BAD_REQUEST),
    UNKNOWN_RESPONSE_TYPE(1008, HttpStatus.BAD_REQUEST),
    UNSUPPORTED_RESPONSE_TYPE(1009, HttpStatus.BAD_REQUEST),

    INVALID_REDIRECT_URI(1010, HttpStatus.BAD_REQUEST),
    UNSUPPORTED_REDIRECT_URI(1011, HttpStatus.BAD_REQUEST),

    UNSUPPORTED_SCOPE(1012, HttpStatus.BAD_REQUEST),

    INVALID_ACCESS_TOKEN(1013, HttpStatus.UNAUTHORIZED),
    ACCESS_TOKEN_EXPIRED(1014, HttpStatus.UNAUTHORIZED),

    INVALID_REFRESH_TOKEN(1015, HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED(1016, HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_FAILED(1017, HttpStatus.INTERNAL_SERVER_ERROR),

    AUTHORIZATION_EXPIRED(1018, HttpStatus.UNAUTHORIZED),

    AUTHENTICATE_FAILED(1019, HttpStatus.UNAUTHORIZED),

    INVALID_AUTHENTICATION_CODE(1020, HttpStatus.UNAUTHORIZED),
    GENERATE_AUTHENTICATION_CODE_FAILED(1021, HttpStatus.INTERNAL_SERVER_ERROR),

    GENERATE_OAUTH2_TOKEN_FAILED(1022, HttpStatus.INTERNAL_SERVER_ERROR),

    REFRESH_TOKEN_NOT_ALLOWED(1023, HttpStatus.BAD_REQUEST),

    INVALID_STATE(1024, HttpStatus.UNAUTHORIZED),

    ;

    private final int domainCode;
    private final HttpStatus httpStatus;

    @Override
    public String getDomain() {
        return "OAUTH2";
    }

    @Override
    public String getMessage() {
        return name();
    }

}
