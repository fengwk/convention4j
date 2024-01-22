package fun.fengwk.convention4j.oauth2.sdk.client.impl;

import com.google.gson.reflect.TypeToken;
import fun.fengwk.convention4j.api.code.CommonErrorCodes;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.gson.GsonUtils;
import fun.fengwk.convention4j.common.result.Results;
import fun.fengwk.convention4j.oauth2.sdk.client.RevokeTokenClient;
import fun.fengwk.convention4j.oauth2.sdk.config.OAuth2SdkProperties;
import fun.fengwk.convention4j.oauth2.share.constant.TokenType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * 使用{@link HttpClient}实现的OAuth2客户端
 *
 * @author fengwk
 */
@Slf4j
@AllArgsConstructor
public class RevokeTokenHttpClient implements RevokeTokenClient {

    private final OAuth2HttpClientManager oauth2HttpClientManager;
    private final OAuth2SdkProperties oauth2SdkProperties;

    @Override
    public Result<Void> revokeToken(String accessToken) {
        URI subjectUri = UriComponentsBuilder.
            fromUriString(oauth2SdkProperties.getOauth2ApiBaseUri() + "/token")
            .build().toUri();
        HttpRequest postReq = HttpRequest.newBuilder(subjectUri)
            .setHeader(TokenType.AUTHORIZATION, TokenType.BEARER.buildAuthorization(accessToken))
            .DELETE()
            .build();
        HttpClient httpClient = oauth2HttpClientManager.getHttpClient();
        try {
            HttpResponse<String> resp = httpClient.send(postReq, HttpResponse.BodyHandlers.ofString());
            String respBodyJson = resp.body();
            return GsonUtils.fromJson(respBodyJson, new TypeToken<Result<Void>>() {}.getType());
        } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
            return Results.error(CommonErrorCodes.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            log.error("Failed to send revoke token request", ex);
            return Results.error(CommonErrorCodes.INTERNAL_SERVER_ERROR);
        }
    }
}
