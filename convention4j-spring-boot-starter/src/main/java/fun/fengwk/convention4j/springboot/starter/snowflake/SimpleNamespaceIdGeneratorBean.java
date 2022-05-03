package fun.fengwk.convention4j.springboot.starter.snowflake;

import fun.fengwk.convention4j.common.idgen.IdGenerator;
import fun.fengwk.convention4j.common.idgen.SimpleNamespaceIdGenerator;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.function.Function;

/**
 * @author fengwk
 */
public class SimpleNamespaceIdGeneratorBean<ID> extends SimpleNamespaceIdGenerator<ID> implements InitializingBean, DisposableBean {

    public SimpleNamespaceIdGeneratorBean(Function<String, IdGenerator<ID>> idGeneratorFactory) {
        super(idGeneratorFactory);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
        start();
    }

    @Override
    public void destroy() throws Exception {
        stop();
        close();
    }

}
