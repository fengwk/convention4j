#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.core.model;

import ${package}.share.model.DemoCreateDTO;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author fengwk
 */
@Data
public class Demo {

    private long id;
    private String name;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static Demo create(long id, DemoCreateDTO createDTO) {
        Demo demo = new Demo();
        demo.setId(id);
        demo.setName(createDTO.getName());
        LocalDateTime now = LocalDateTime.now();
        demo.setCreateTime(now);
        demo.setUpdateTime(now);
        return demo;
    }

}
