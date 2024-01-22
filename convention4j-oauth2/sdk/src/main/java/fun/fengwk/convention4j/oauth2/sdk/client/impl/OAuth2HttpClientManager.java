package fun.fengwk.convention4j.oauth2.sdk.client.impl;

import fun.fengwk.convention4j.common.concurrent.NamedThreadFactory;
import fun.fengwk.convention4j.oauth2.sdk.config.HttpClientConfig;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author fengwk
 */
public class OAuth2HttpClientManager {

    private final HttpClient httpClient;

    public OAuth2HttpClientManager(HttpClientConfig httpClientConfig) {
        NamedThreadFactory namedThreadFactory = new NamedThreadFactory("OAuth2-Http-Client");
        ExecutorService oauth2HttpClientExecutorService = new ThreadPoolExecutor(
            httpClientConfig.getExecuteThreadCount(), httpClientConfig.getExecuteThreadCount(),
            0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(httpClientConfig.getExecuteQueueSize()),
            namedThreadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
        this.httpClient = HttpClient.newBuilder()
            .executor(oauth2HttpClientExecutorService)
            .connectTimeout(Duration.ofMillis(httpClientConfig.getConnectTimeoutMs()))
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

}
