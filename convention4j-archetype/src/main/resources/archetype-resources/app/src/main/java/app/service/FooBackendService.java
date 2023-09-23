#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.app.service;

import fun.fengwk.convention4j.common.NullSafe;
import ${package}.app.converter.FooConverter;
import ${package}.domain.model.FooBO;
import ${package}.domain.model.FooCreateBO;
import ${package}.domain.repo.FooRepository;
import ${package}.domain.service.CreateFooService;
import ${package}.share.model.FooCreateDTO;
import ${package}.share.model.FooDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author fengwk
 */
@Slf4j
@AllArgsConstructor
@Service
public class FooBackendService {

    private final CreateFooService createFooService;
    private final FooRepository fooRepository;

    public String createFoo(FooCreateDTO createDTO) {
        FooCreateBO createBO = FooConverter.INSTANCE.convert(createDTO);
        FooBO fooBO = createFooService.create(createBO);
        return NullSafe.map(fooBO, FooBO::getId);
    }

    public FooDTO getByFooId(String fooId) {
        FooBO fooBO = fooRepository.getById(fooId);
        return FooConverter.INSTANCE.convert(fooBO);
    }

}
