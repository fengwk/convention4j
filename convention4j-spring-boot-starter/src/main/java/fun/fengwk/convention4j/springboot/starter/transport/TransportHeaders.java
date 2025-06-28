package fun.fengwk.convention4j.springboot.starter.transport;

import java.util.Set;

/**
 * @author fengwk
 */
public interface TransportHeaders {

    /**
     * 添加要传输的请求头
     *
     * @param headerName 请求头名称
     */
    void addHeader(String headerName);

    /**
     * 删除要传输的请求头
     *
     * @param headerName 请求头名称
     */
    void removeHeader(String headerName);

    /**
     * 获取所有要传输的请求头视图
     *
     * @return 所有要传输的请求头视图
     */
    Set<String> viewHeaders();

}
