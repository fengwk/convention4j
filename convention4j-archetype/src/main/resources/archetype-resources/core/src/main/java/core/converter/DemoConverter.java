#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.core.converter;

import ${package}.core.model.Demo;
import ${package}.share.model.DemoDTO;
import org.springframework.stereotype.Component;

/**
 * @author fengwk
 */
@Component
public class DemoConverter {

    public DemoDTO convert(Demo demo) {
        if (demo == null) {
            return null;
        }
        DemoDTO demoDTO = new DemoDTO();
        demoDTO.setId(demo.getId());
        demoDTO.setName(demo.getName());
        demoDTO.setCreateTime(demo.getCreateTime());
        demoDTO.setUpdateTime(demo.getUpdateTime());
        return demoDTO;
    }

}
