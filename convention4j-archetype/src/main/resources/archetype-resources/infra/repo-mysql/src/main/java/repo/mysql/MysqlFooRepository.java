#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.repo.mysql;

import fun.fengwk.convention4j.common.idgen.NamespaceIdGenerator;
import ${package}.core.domain.model.FooBO;
import ${package}.core.domain.repo.FooRepository;
import ${package}.repo.mysql.converter.FooConverter;
import ${package}.repo.mysql.mapper.FooMapper;
import ${package}.repo.mysql.model.FooPO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * @author fengwk
 */
@AllArgsConstructor
@Repository
public class MysqlFooRepository implements FooRepository {

    private final NamespaceIdGenerator<Long> idGenerator;
    private final FooMapper fooMapper;

    @Override
    public String allocateId() {
        return String.valueOf(idGenerator.next(getClass()));
    }

    @Override
    public boolean insert(FooBO fooBO) {
        FooPO fooPO = FooConverter.INSTANCE.convert(fooBO);
        return fooMapper.insert(fooPO) > 0;
    }

    @Override
    public FooBO getById(String id) {
        FooPO fooPO = fooMapper.findById(id);
        return FooConverter.INSTANCE.convert(fooPO);
    }

}
