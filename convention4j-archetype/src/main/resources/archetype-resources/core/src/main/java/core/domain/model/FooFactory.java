#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.core.domain.model;

import ${package}.core.domain.repo.FooRepository;
import ${package}.share.constant.FooErrorCode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author fengwk
 */
@Slf4j
@AllArgsConstructor
@Component
public class FooFactory {

    private final FooRepository fooRepository;

    public FooBO create(FooCreateBO createBO) {
        if (createBO == null) {
            log.warn("createBO can't be null");
            throw FooErrorCode.INVALID_PARAM.asThrowable();
        }
        FooBO fooBO = new FooBO();
        fooBO.setId(fooRepository.allocateId());
        fooBO.setName(createBO.getName());
        fooBO.setStatus(createBO.getStatus());
        return fooBO;
    }

}
