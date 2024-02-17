package fun.fengwk.convention4j.common.json.gson;

import com.google.gson.GsonBuilder;
import fun.fengwk.convention4j.common.OrderedObject;

/**
 * GsonBuilder的配置器，为GsonBuilder配置提供SPI扩展能力。
 *
 * @author fengwk
 */
public interface GsonBuilderConfigurator extends OrderedObject {

    /**
     * 构建后完成初始化
     */
    default void init() {};

    /**
     * 配置指定的GsonBuilder。
     *
     * @param builder not null
     */
    void config(GsonBuilder builder);

}
