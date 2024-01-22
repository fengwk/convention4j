#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.core.service;

import fun.fengwk.convention4j.api.page.Page;
import fun.fengwk.convention4j.api.page.PageQuery;
import ${package}.core.converter.DemoConverter;
import ${package}.core.model.Demo;
import ${package}.core.repo.DemoRepository;
import ${package}.share.constant.DemoErrorCodes;
import ${package}.share.model.DemoCreateDTO;
import ${package}.share.model.DemoDTO;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author fengwk
 */
@Slf4j
@AllArgsConstructor
@Service
public class DemoServiceImpl implements DemoService {

    private final DemoConverter demoConverter;
    private final DemoRepository demoRepository;

    @PostConstruct
    public void init() {
        demoRepository.init();
    }

    @Override
    public DemoDTO createDemo(DemoCreateDTO createDTO) {
        Demo demo = Demo.create(demoRepository.generateId(), createDTO);
        if (!demoRepository.add(demo)) {
            log.error("Add demo failed, demo: {}", demo);
            throw DemoErrorCodes.CREATE_DEMO_FAILED.asThrowable();
        }
        return demoConverter.convert(demo);
    }

    @Override
    public void removeDemo(long id) {
        if (!demoRepository.removeById(id)) {
            log.error("Remove demo failed, id: {}", id);
            throw DemoErrorCodes.REMOVE_DEMO_FAILED.asThrowable();
        }
    }

    @Override
    public Page<DemoDTO> pageDemo(PageQuery pageQuery) {
        return demoRepository.page(pageQuery).map(demoConverter::convert);
    }

}
