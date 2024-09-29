package fun.fengwk.convention4j.oauth2.share.constant;

import fun.fengwk.convention4j.api.code.DomainConventionErrorCodeEnumAdapter;
import fun.fengwk.convention4j.api.code.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * OAuth4j错误码
 * @author fengwk
 */
@Getter
@AllArgsConstructor
public enum OAuth2ErrorCodes implements DomainConventionErrorCodeEnumAdapter {

    INVALID_REDIRECT_URI(HttpStatus.BAD_REQUEST),
    UNSUPPORTED_GRANT_TYPE(HttpStatus.BAD_REQUEST),
    UNSUPPORTED_RESPONSE_TYPE(HttpStatus.BAD_REQUEST),
    UNSUPPORTED_REDIRECT_URI(HttpStatus.BAD_REQUEST),
    UNSUPPORTED_SCOPE(HttpStatus.BAD_REQUEST),

    INVALID_CLIENT_SECRET(HttpStatus.UNAUTHORIZED),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED),
    AUTHORIZE_EXPIRED(HttpStatus.UNAUTHORIZED),
    AUTHENTICATE_FAILED(HttpStatus.UNAUTHORIZED),
    INVALID_AUTHENTICATION_CODE(HttpStatus.UNAUTHORIZED),
    INVALID_STATE(HttpStatus.UNAUTHORIZED),

    CLIENT_NOT_FOUND(HttpStatus.NOT_FOUND),

    REFRESH_TOKEN_FAILED(HttpStatus.INTERNAL_SERVER_ERROR),
    GENERATE_AUTHENTICATION_CODE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR),
    GENERATE_OAUTH2_TOKEN_FAILED(HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final HttpStatus httpStatus;

    @Override
    public String getDomain() {
        return "OAUTH2";
    }

}
