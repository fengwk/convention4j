package fun.fengwk.convention4j.springboot.starter.datasource.multi.ds1;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author fengwk
 */
@AllArgsConstructor
@Component
public class Ds1Manager {

    private final Ds1Mapper ds1Mapper;

    // primary无需配置
    @Transactional
    public Ds1DO insertAndQuery(String name) {
        Ds1DO ds1DO = new Ds1DO();
        ds1DO.populateDefaultFields();
        ds1DO.setName(name);
        ds1Mapper.insert(ds1DO);
        return ds1Mapper.getByName(name);
    }

}
