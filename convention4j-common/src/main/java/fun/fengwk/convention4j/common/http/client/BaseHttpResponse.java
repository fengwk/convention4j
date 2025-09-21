package fun.fengwk.convention4j.common.http.client;

import java.util.List;

/**
 * @author fengwk
 */
public interface BaseHttpResponse {

    boolean is2xx();

    boolean is3xx();

    boolean is4xx();

    boolean is5xx();

    List<String> getHeaders(String name);

    String getFirstHeader(String name);

}
