package fun.fengwk.convention4j.springboot.starter.xheader;

import java.util.Set;

/**
 * 自定义Header注册表
 *
 * @author fengwk
 */
public interface XHeaderRegistry {

    /**
     * 添加自定义Header名称
     *
     * @param name 自定义Header名称
     */
    void addXHeaderName(String name);

    /**
     * 获取所有自定义Header名称
     *
     * @return 自定义Header名称集合
     */
    Set<String> getXHeaderNames();

}
