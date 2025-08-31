package fun.fengwk.convention4j.common.web;

/**
 * @author fengwk
 */
public final class SameSite {

    private SameSite() {
    }

    /**
     * 严格模式，不允许在跨站请求中发送cookie
     */
    public static final String STRICT = "Strict";
    /**
     * 宽松模式，允许在跨站请求中发送cookie
     */
    public static final String LAX = "Lax";
    /**
     * 不设置SameSite属性，允许在跨站请求中发送cookie
     */
    public static final String NONE = "None";

}
