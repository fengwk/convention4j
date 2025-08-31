package fun.fengwk.convention4j.spring.cloud.starter.gateway.filter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author fengwk
 */
@Data
@ConfigurationProperties("convention.spring-cloud-gateway.x-forwarded-header")
public class XForwardedHeaderProperties {

    /**
     * 是否启用 XForwardedHeaderGlobalFilter。
     */
    private boolean enabled = true;

    /**
     * 是否启用 X-Forwarded-* 系列头部的设置。
     * 包括：X-Forwarded-For, X-Forwarded-Proto, X-Forwarded-Host, X-Forwarded-Port。
     */
    private boolean xForwardedEnabled = true;

    /**
     * 是否启用 RFC 7239 标准的 Forwarded 头部。
     * 推荐在新项目中使用，但为了兼容性，可能需要与 X-Forwarded-* 同时启用。
     */
    private boolean forwardedEnabled = false;

    /**
     * 是否启用 X-Original-URI 头部。
     * 当网关执行URL路径重写时，此头部非常有用。
     */
    private boolean originalUriEnabled = true;

    /**
     * 是否启用 RFC 7230 标准的 Via 头部。
     * 推荐启用，以遵循HTTP标准并支持请求追踪。
     */
    private boolean viaEnabled = true;

    /**
     * 是否启用真实客户端端口
     */
    private boolean realPortEnabled = true;

}
