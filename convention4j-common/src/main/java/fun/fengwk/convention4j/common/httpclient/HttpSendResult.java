package fun.fengwk.convention4j.common.httpclient;

import fun.fengwk.convention4j.common.util.ListUtils;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author fengwk
 */
@Data
public class HttpSendResult {

    private int statusCode;
    private String body;
    private Map<String, List<String>> headers;
    private Throwable error;

    public boolean is2xx() {
        return HttpClientUtils.is2xx(statusCode);
    }

    public boolean hasError() {
        return error != null;
    }

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

    public String getFirstHeader(String name) {
        List<String> headers = getHeaders(name);
        return ListUtils.tryGetFirst(headers);
    }

}
