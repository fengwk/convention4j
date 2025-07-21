package fun.fengwk.convention4j.spring.cloud.starter.gateway.filter;

import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.common.path.PathParser;
import fun.fengwk.convention4j.common.tika.ThreadLocalTika;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import static org.springframework.cloud.gateway.support.GatewayToStringStyler.filterToStringCreator;
import static org.springframework.http.HttpHeaders.CONTENT_ENCODING;
import static org.springframework.http.HttpHeaders.TRANSFER_ENCODING;

/**
 * 用于处理资源文件的GatewayFilterFactory
 *
 * @author fengwk
 */
@Slf4j
@AllArgsConstructor
public class ResourceGatewayFilterFactory implements GatewayFilterFactory<ResourceGatewayFilterFactory.Config> {

    /**
     * 默认chunk块大小，该参数有助有优化TCP网络交互，size太小会使传输速度显著下降
     */
    private static final int DEFAULT_CHUNK_SIZE = 1024 * 16;
    private static final String GZIP = "gzip";
    private static final String CHUNKED = "chunked";
    private static final PathParser PATH_PARSER = new PathParser();
    private static final String DEFAULT_404_PAGE = "classpath:/static/404-default.html";
    private static final String USER_404_PAGE = "classpath:/static/404.html";

    private final ResourceLoader resourceLoader;

    @Override
    public Class<Config> getConfigClass() {
        return Config.class;
    }

    @Override
    public Config newConfig() {
        return new Config();
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("index", "chunkSize");
    }

    @Override
    public GatewayFilter apply(Config config) {
        Resource resource404 = get404PageResource();

        return new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
                if (route == null || route.getUri() == null) {
                    HttpHeaders reqHeaders = exchange.getRequest().getHeaders();
                    HttpHeaders respHeaders = exchange.getResponse().getHeaders();
                    boolean gzip = supportGzip(reqHeaders, respHeaders);
                    return response404(exchange, resource404, config.getChunkSize(), gzip);
                }

                String uri = route.getUri().toString();
                uri = fixUri(uri);
                Resource baseResource = resourceLoader.getResource(uri);

                // 首先从路径中读取，路径中不存在使用index尝试读取
                String path = exchange.getRequest().getPath().pathWithinApplication().value();
                Resource resource = createReadableRelativeResource(
                        baseResource, asRelativePath(PATH_PARSER.normalize(path)));
                if (resource == null && StringUtils.isNotBlank(config.getIndex())) {
                    resource = createReadableRelativeResource(
                            baseResource, getRelativePath(path, config.getIndex()));
                }

                // 获取请求响应信息
                HttpHeaders reqHeaders = exchange.getRequest().getHeaders();
                ServerHttpResponse response = exchange.getResponse();
                HttpHeaders respHeaders = response.getHeaders();

                // 提供gzip支持
                boolean gzip = supportGzip(reqHeaders, respHeaders);

                // 找不到资源返回404错误页面
                if (resource == null) {
                    return response404(exchange, resource404, config.getChunkSize(), gzip);
                }

                // 设置资源类型
                respHeaders.setContentType(getContentType(resource));

                // 非gzip情况设置contentLength
                if (!gzip) {
                    long contentLength = getContentLength(resource);
                    if (contentLength >= 0) {
                        respHeaders.setContentLength(contentLength);
                    }
                }

                // 支持lastModified
                long lastModified = getLastModified(resource);
                respHeaders.setLastModified(lastModified);
                if (lastModified > 0) {
                    long ifModifiedSince = reqHeaders.getIfModifiedSince();
                    // ifModifiedSince不计算毫秒级别，所以这里除以1000再比较
                    if ((ifModifiedSince / 1000) == (lastModified / 1000)) {
                        return response304(exchange);
                    }
                }

                response.setStatusCode(HttpStatus.OK);
                return writeResource(response, resource, config.getChunkSize(), gzip);
            }

            @Override
            public String toString() {
                return filterToStringCreator(ResourceGatewayFilterFactory.this)
                        .append("index", config.getIndex())
                        .append("chunkSize", config.getChunkSize())
                        .toString();
            }
        };
    }

    private Resource get404PageResource() {
        Resource resource404 = resourceLoader.getResource(USER_404_PAGE);
        if (!resource404.isReadable()) {
            resource404 = resourceLoader.getResource(DEFAULT_404_PAGE);
            if (!resource404.isReadable()) {
                throw new IllegalStateException("404.html not found");
            }
        }
        return resource404;
    }

    private String fixUri(String uri) {
        // 修复file:///home/fengwk/Pictures时不作为一个目录处理的情况
        if (uri.startsWith("file:") && !uri.endsWith("/")) {
            uri += "/";
        }
        return uri;
    }

    private boolean supportGzip(HttpHeaders reqHeaders, HttpHeaders respHeaders) {
        // 提供gzip支持
        String acceptEncoding = reqHeaders.getFirst(HttpHeaders.ACCEPT_ENCODING);
        boolean gzip = isAcceptGzip(acceptEncoding);
        if (gzip) {
            respHeaders.set(CONTENT_ENCODING, GZIP);
            respHeaders.set(TRANSFER_ENCODING, CHUNKED);
        }
        return gzip;
    }

    private long getContentLength(Resource resource) {
        try {
            return resource.contentLength();
        } catch (IOException ignore) {
            return -1;
        }
    }

    private boolean isAcceptGzip(String acceptEncoding) {
        return acceptEncoding != null && acceptEncoding.contains(GZIP);
    }

    private long getLastModified(Resource resource) {
        try {
            return resource.lastModified();
        } catch (IOException ignore) {
            return 0;
        }
    }

    private GZIPOutputStream gzipWrap(OutputStream output) {
        try {
            return new GZIPOutputStream(output);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void write(OutputStream output, byte b) {
        try {
            output.write(b);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void gzipFinish(GZIPOutputStream gzipOutput) {
        try {
            gzipOutput.finish();
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void close(OutputStream output) {
        try {
            output.close();
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private Mono<Void> writeResource(ServerHttpResponse response, Resource resource, int chunkSize, boolean gzip) {
        // 读取资源为Flux<DataBuffer>
        Flux<DataBuffer> dataBufferFlux = DataBufferUtils.read(resource, response.bufferFactory(), chunkSize);

        if (gzip) {
            // 如果开启了gzip将每个DataBuffer映射为压缩后的DataBuffer
            ByteArrayOutputStream bytesOutput = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutput = gzipWrap(bytesOutput);
            dataBufferFlux = dataBufferFlux
                    // 使用concatMap确保顺序性
                    .concatMap(dataBuffer -> {
                        // synchronized确保可见性
                        synchronized (gzipOutput) {
                            while (dataBuffer.readableByteCount() > 0) {
                                write(gzipOutput, dataBuffer.read());
                            }
                            byte[] buf = bytesOutput.toByteArray();
                            bytesOutput.reset();
                            DataBuffer gzipDataBuf = dataBuffer.factory().wrap(buf);
                            DataBufferUtils.release(dataBuffer);
                            return Mono.just(gzipDataBuf);
                        }
                    });
            dataBufferFlux = Flux.concat(dataBufferFlux, Mono.defer(() -> {
                // synchronized确保可见性
                synchronized (gzipOutput) {
                    // 最终去调用gzip finish发送0数据块
                    gzipFinish(gzipOutput);
                    byte[] buf = bytesOutput.toByteArray();
                    DataBuffer finishBuf = response.bufferFactory().wrap(buf);
                    return Mono.just(finishBuf);
                }
            }));
            // 写入所有Flux<DataBuffer>
            return response.writeWith(dataBufferFlux)
                    // 定义DataBuffer释放方法
                    .doOnDiscard(DataBuffer.class, DataBufferUtils::release)
                    .doFinally(s -> {
                        synchronized (gzipOutput) {
                            close(gzipOutput);
                        }
                    });
        } else {
            // 写入所有Flux<DataBuffer>
            return response.writeWith(dataBufferFlux)
                    // 定义DataBuffer释放方法
                    .doOnDiscard(DataBuffer.class, DataBufferUtils::release);
        }
    }

    private Resource createReadableRelativeResource(Resource baseResource, String relativePath) {
        try {
            Resource resource = baseResource.createRelative(relativePath);
            if (resource.isReadable()) {
                return resource;
            }
        } catch (IOException ignore) {
        }
        return null;
    }

    private Mono<Void> response304(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.NOT_MODIFIED);
        return response.setComplete();
    }

    private Mono<Void> response404(ServerWebExchange exchange, Resource resource404, int chunkSize, boolean gzip) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.NOT_FOUND);
        response.getHeaders().setContentType(getContentType(resource404));
        return writeResource(response, resource404, chunkSize, gzip);
    }

    private MediaType getContentType(Resource resource) {
        try {
            // 使用JDK Files读取文件类型
            MediaType mediaType = null;
            if (resource.isFile()) {
                Path filePath = resource.getFile().toPath();
                String contentType = Files.probeContentType(filePath);
                if (contentType != null) {
                    mediaType = MediaType.parseMediaType(contentType);
                } else if (filePath.getFileName().endsWith("map")) {
                    mediaType = MediaType.parseMediaType("application/json map");
                }
            }
            // 使用Tika作为降级方案读取文件类型
            // 比如JDK无法解析或流形式非文件形式的Resource
            if (mediaType == null) {
                try (InputStream inputStream = resource.getInputStream()) {
                    String mimeType = ThreadLocalTika.current().detect(inputStream);
                    if (StringUtils.isNotEmpty(mimeType)) {
                        mediaType = MediaType.parseMediaType(mimeType);
                    }
                }
            }
            return mediaType;
        } catch (Exception ex) {
            log.debug("Failed to get content type for resource: {}", resource, ex);
        }
        return MediaType.parseMediaType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
    }

    private String getRelativePath(String path, String index) {
        fun.fengwk.convention4j.common.path.Path pIndex = PATH_PARSER.normalize(index);
        if (pIndex.isAbsolute()) {
            return asRelativePath(pIndex);
        } else {
            fun.fengwk.convention4j.common.path.Path base = PATH_PARSER.normalize(path);
            String fullPath = base.getPath() + PATH_PARSER.getSeparator() + pIndex.getPath();
            return asRelativePath(PATH_PARSER.normalize(fullPath));
        }
    }

    private String asRelativePath(fun.fengwk.convention4j.common.path.Path p) {
        return String.join(PATH_PARSER.getSeparator(), p.getSegments());
    }

    @Data
    public static class Config {

        private String index;
        private int chunkSize = DEFAULT_CHUNK_SIZE;

    }

}
