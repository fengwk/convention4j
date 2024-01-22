package fun.fengwk.convention4j.oauth2.sdk.client.impl;

import com.google.gson.internal.$Gson$Types;
import fun.fengwk.convention4j.api.code.CommonErrorCodes;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.NullSafe;
import fun.fengwk.convention4j.common.gson.GsonUtils;
import fun.fengwk.convention4j.common.result.Results;
import fun.fengwk.convention4j.oauth2.sdk.client.SubjectClient;
import fun.fengwk.convention4j.oauth2.sdk.config.OAuth2SdkProperties;
import fun.fengwk.convention4j.oauth2.share.constant.TokenType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Type;
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
public class SubjectHttpClient<SUBJECT> implements SubjectClient<SUBJECT> {

    private final OAuth2HttpClientManager oauth2HttpClientManager;
    private final OAuth2SdkProperties oauth2SdkProperties;
    private final Type subjectResultType;

    public SubjectHttpClient(OAuth2HttpClientManager oauth2HttpClientManager,
                             OAuth2SdkProperties oauth2SdkProperties,
                             Type subjectType) {
        this.oauth2HttpClientManager = oauth2HttpClientManager;
        this.oauth2SdkProperties = oauth2SdkProperties;
        this.subjectResultType = $Gson$Types.newParameterizedTypeWithOwner(null, Result.class, subjectType);
    }

    @Override
    public Result<SUBJECT> subject(String accessToken, String scope) {
        URI subjectUri = UriComponentsBuilder.
            fromUriString(oauth2SdkProperties.getOauth2ApiBaseUri() + "/subject")
            .queryParam("scope", NullSafe.of(scope, ""))
            .build().toUri();
        HttpRequest postReq = HttpRequest.newBuilder(subjectUri)
            .setHeader(TokenType.AUTHORIZATION, TokenType.BEARER.buildAuthorization(accessToken))
            .GET()
            .build();
        HttpClient httpClient = oauth2HttpClientManager.getHttpClient();
        try {
            HttpResponse<String> resp = httpClient.send(postReq, HttpResponse.BodyHandlers.ofString());
            String respBodyJson = resp.body();
            return GsonUtils.fromJson(respBodyJson, subjectResultType);
        } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
            return Results.error(CommonErrorCodes.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            log.error("Failed to send token request", ex);
            return Results.error(CommonErrorCodes.INTERNAL_SERVER_ERROR);
        }
    }

}
