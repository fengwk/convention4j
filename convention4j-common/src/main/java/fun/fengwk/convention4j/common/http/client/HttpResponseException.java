package fun.fengwk.convention4j.common.http.client;

import java.util.List;
import java.util.Objects;

/**
 * @author fengwk
 */
public class HttpResponseException extends RuntimeException implements BaseHttpResponse {

    private static final long serialVersionUID = 1L;

    private final BaseHttpResponse delegate;

    public HttpResponseException(Throwable cause, BaseHttpResponse delegate) {
        super(cause);
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
    }

    @Override
    public boolean is2xx() {
        return delegate.is2xx();
    }

    @Override
    public boolean is3xx() {
        return delegate.is3xx();
    }

    @Override
    public boolean is4xx() {
        return delegate.is4xx();
    }

    @Override
    public boolean is5xx() {
        return delegate.is5xx();
    }

    @Override
    public List<String> getHeaders(String name) {
        return delegate.getHeaders(name);
    }

    @Override
    public String getFirstHeader(String name) {
        return delegate.getFirstHeader(name);
    }

}
