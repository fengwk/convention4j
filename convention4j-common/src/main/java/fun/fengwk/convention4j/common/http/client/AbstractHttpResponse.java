package fun.fengwk.convention4j.common.http.client;

import fun.fengwk.convention4j.common.http.HttpUtils;
import fun.fengwk.convention4j.common.util.ListUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author fengwk
 */
@Slf4j
@Data
public class AbstractHttpResponse implements BaseHttpResponse {

    private int statusCode;
    private Map<String, List<String>> headers;

    @Override
    public boolean is2xx() {
        return HttpUtils.is2xx(statusCode);
    }

    @Override
    public boolean is3xx() {
        return HttpUtils.is3xx(statusCode);
    }

    @Override
    public boolean is4xx() {
        return HttpUtils.is4xx(statusCode);
    }

    @Override
    public boolean is5xx() {
        return HttpUtils.is5xx(statusCode);
    }

    @Override
    public List<String> getHeaders(String name) {
        if (name == null) {
            return Collections.emptyList();
        }

        if (headers == null) {
            return Collections.emptyList();
        }

        List<String> list = headers.get(name);
        if (list != null) {
            return list;
        }

        for (String key : headers.keySet()) {
            if (name.equalsIgnoreCase(key)) {
                return headers.get(key);
            }
        }

        return Collections.emptyList();
    }

    @Override
    public String getFirstHeader(String name) {
        List<String> headers = getHeaders(name);
        return ListUtils.tryGetFirst(headers);
    }

}
