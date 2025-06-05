package fun.fengwk.convention4j.ai.tool;

import lombok.Data;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author fengwk
 */
@Data
public class JsonSchema {

    private String type;
    private String description;
    private LinkedHashMap<String, JsonSchema> properties;
    private JsonSchema items;
    private List<String> required;

    /**
     * 不是JsonSchema规范，仅为了存储类型字段
     */
    private Type javaType;

}
