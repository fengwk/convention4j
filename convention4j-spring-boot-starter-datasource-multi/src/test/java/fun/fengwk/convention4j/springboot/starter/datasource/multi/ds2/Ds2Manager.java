package fun.fengwk.convention4j.springboot.starter.datasource.multi.ds2;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author fengwk
 */
@AllArgsConstructor
@Component
public class Ds2Manager {

    private final Ds2Mapper ds2Mapper;

    // 需要配置TransactionManager，否则事务将无法生效
    @Transactional("ds2TransactionManager")
    public Ds2DO insertAndQuery(String name) {
        Ds2DO ds2DO = new Ds2DO();
        ds2DO.populateDefaultFields();
        ds2DO.setName(name);
        ds2Mapper.insert(ds2DO);
        return ds2Mapper.getByName(name);
    }

}
