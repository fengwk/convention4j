package fun.fengwk.convention4j.api.gson;

import com.google.gson.GsonBuilder;
import fun.fengwk.convention4j.common.OrderedObject;

/**
 * GsonBuilder的配置器，为GsonBuilder配置提供SPI扩展能力。
 *
 * @author fengwk
 */
public interface GsonBuilderConfigurator extends OrderedObject {

    /**
     * 配置指定的GsonBuilder。
     *
     * @param builder not null
     */
    void config(GsonBuilder builder);

}
