package fun.fengwk.convention4j.oauth2.sdk.client.impl;

import com.google.gson.reflect.TypeToken;
import fun.fengwk.convention4j.api.code.CommonErrorCodes;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.gson.GsonUtils;
import fun.fengwk.convention4j.common.result.Results;
import fun.fengwk.convention4j.oauth2.sdk.client.RefreshTokenClient;
import fun.fengwk.convention4j.oauth2.sdk.config.OAuth2SdkProperties;
import fun.fengwk.convention4j.oauth2.share.constant.GrantType;
import fun.fengwk.convention4j.oauth2.share.model.OAuth2TokenDTO;
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
public class RefreshTokenHttpClient implements RefreshTokenClient {

    private final OAuth2HttpClientManager oauth2HttpClientManager;
    private final OAuth2SdkProperties oauth2SdkProperties;

    @Override
    public Result<OAuth2TokenDTO> refreshToken(String refreshToken) {
        URI tokenUri = UriComponentsBuilder
            .fromUriString(oauth2SdkProperties.getOauth2ApiBaseUri() + "/token")
            .queryParam("grantType", GrantType.REFRESH_TOKEN.getCode())
            .queryParam("clientId", oauth2SdkProperties.getClientId())
            .queryParam("clientSecret", oauth2SdkProperties.getClientSecret())
            .queryParam("refreshToken", refreshToken)
            .build().toUri();
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.noBody();
        HttpRequest postReq = HttpRequest.newBuilder(tokenUri).POST(body).build();
        HttpClient httpClient = oauth2HttpClientManager.getHttpClient();
        try {
            HttpResponse<String> resp = httpClient.send(postReq, HttpResponse.BodyHandlers.ofString());
            String respBodyJson = resp.body();
            return GsonUtils.fromJson(respBodyJson, new TypeToken<Result<OAuth2TokenDTO>>() {}.getType());
        } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
            return Results.error(CommonErrorCodes.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            log.error("Failed to send token request", ex);
            return Results.error(CommonErrorCodes.INTERNAL_SERVER_ERROR);
        }
    }

}
