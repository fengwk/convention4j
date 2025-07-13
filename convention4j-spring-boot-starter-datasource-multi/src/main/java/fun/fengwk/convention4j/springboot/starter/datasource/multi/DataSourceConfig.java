package fun.fengwk.convention4j.springboot.starter.datasource.multi;

import lombok.Data;

import java.util.Map;

/**
 * @author fengwk
 */
@Data
public class DataSourceConfig {

    /**
     * jdbc驱动名称
     */
    private String driverClassName;

    /**
     * 连接url
     */
    private String url;

    /**
     * 连接用户名
     */
    private String username;

    /**
     * 连接密码
     */
    private String password;

    /**
     * 扩展属性配置
     */
    private Map<String, Object> properties;

}
