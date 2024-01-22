package fun.fengwk.convention4j.oauth2.sdk.client.impl;

import com.google.gson.reflect.TypeToken;
import fun.fengwk.convention4j.api.code.CommonErrorCodes;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.gson.GsonUtils;
import fun.fengwk.convention4j.common.result.Results;
import fun.fengwk.convention4j.oauth2.sdk.client.AuthorizeClient;
import fun.fengwk.convention4j.oauth2.sdk.client.model.AuthorizeParams;
import fun.fengwk.convention4j.oauth2.sdk.config.OAuth2SdkProperties;
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
public class AuthorizeHttpClient<CERTIFICATE> implements AuthorizeClient<CERTIFICATE> {

    private final OAuth2HttpClientManager oauth2HttpClientManager;
    private final OAuth2SdkProperties oauth2SdkProperties;

    @Override
    public Result<String> authorize(AuthorizeParams<CERTIFICATE> params) {
        URI tokenUri = UriComponentsBuilder
            .fromUriString(oauth2SdkProperties.getOauth2ApiBaseUri() + "/authorize")
            .queryParam("responseType", params.getResponseType())
            .queryParam("clientId", oauth2SdkProperties.getClientId())
            .queryParam("redirectUri", params.getRedirectUri())
            .queryParam("scope", params.getScope())
            .queryParam("state", params.getState())
            .build().toUri();
        HttpRequest.BodyPublisher body;
        if (params.getCertificate() == null) {
            body = HttpRequest.BodyPublishers.noBody();
        } else {
            String certificateJson = GsonUtils.toJson(params.getCertificate());
            body = HttpRequest.BodyPublishers.ofString(certificateJson);
        }
        HttpRequest postReq = HttpRequest.newBuilder(tokenUri).POST(body).build();
        HttpClient httpClient = oauth2HttpClientManager.getHttpClient();
        try {
            HttpResponse<String> resp = httpClient.send(postReq, HttpResponse.BodyHandlers.ofString());
            String respBodyJson = resp.body();
            return GsonUtils.fromJson(respBodyJson, new TypeToken<Result<String>>() {}.getType());
        } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
            return Results.error(CommonErrorCodes.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            log.error("Failed to send authorize request", ex);
            return Results.error(CommonErrorCodes.INTERNAL_SERVER_ERROR);
        }
    }

}
