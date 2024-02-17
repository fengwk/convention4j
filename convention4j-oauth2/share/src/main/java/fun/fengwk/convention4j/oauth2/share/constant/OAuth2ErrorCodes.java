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

    INVALID_REDIRECT_URI(1000, HttpStatus.BAD_REQUEST),
    UNSUPPORTED_GRANT_TYPE(1001, HttpStatus.BAD_REQUEST),
    UNSUPPORTED_RESPONSE_TYPE(1002, HttpStatus.BAD_REQUEST),
    UNSUPPORTED_REDIRECT_URI(1003, HttpStatus.BAD_REQUEST),
    UNSUPPORTED_SCOPE(1004, HttpStatus.BAD_REQUEST),

    INVALID_CLIENT_SECRET(1100, HttpStatus.UNAUTHORIZED),
    INVALID_ACCESS_TOKEN(1101, HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN(1102, HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED(1103, HttpStatus.UNAUTHORIZED),
    AUTHORIZE_EXPIRED(1104, HttpStatus.UNAUTHORIZED),
    AUTHENTICATE_FAILED(1105, HttpStatus.UNAUTHORIZED),
    INVALID_AUTHENTICATION_CODE(1106, HttpStatus.UNAUTHORIZED),
    INVALID_STATE(1107, HttpStatus.UNAUTHORIZED),

    CLIENT_NOT_FOUND(1200, HttpStatus.NOT_FOUND),

    REFRESH_TOKEN_FAILED(2000, HttpStatus.INTERNAL_SERVER_ERROR),
    GENERATE_AUTHENTICATION_CODE_FAILED(2001, HttpStatus.INTERNAL_SERVER_ERROR),
    GENERATE_OAUTH2_TOKEN_FAILED(2002, HttpStatus.INTERNAL_SERVER_ERROR),

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
