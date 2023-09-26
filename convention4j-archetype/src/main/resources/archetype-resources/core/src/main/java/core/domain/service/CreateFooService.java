#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.core.domain.service;

import ${package}.core.domain.model.FooBO;
import ${package}.core.domain.model.FooCreateBO;
import ${package}.core.domain.model.FooFactory;
import ${package}.core.domain.repo.FooRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author fengwk
 */
@AllArgsConstructor
@Service
public class CreateFooService {

    private final FooFactory fooFactory;
    private final FooRepository fooRepository;

    public FooBO create(FooCreateBO createBO) {
        FooBO fooBO = fooFactory.create(createBO);
        if (!fooRepository.insert(fooBO)) {
            return null;
        }
        return fooBO;
    }

}
