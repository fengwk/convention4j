package fun.fengwk.convention4j.common.json.jackson;

import com.google.auto.service.AutoService;

/**
 * @author fengwk
 */
@AutoService(ObjectMapperConfigurator.class)
public class JaxrsObjectMapperConfigurator extends BaseModuleObjectMapperConfigurator {

    @Override
    protected String moduleClassName() {
        return "com.fasterxml.jackson.datatype.jaxrs.Jaxrs2TypesModule";
    }

}
