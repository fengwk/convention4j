package fun.fengwk.convention4j.springboot.starter.transport;

import fun.fengwk.convention4j.common.util.NullSafe;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * @author fengwk
 */
@AutoConfiguration
public class TransportHeadersAutoConfiguration {

    @Bean
    public TransportHeaders transportHeaders(ObjectProvider<List<TransportHeadersModifier>> modifiersProvider) {
        TransportHeaders transportHeaders = new DefaultTransportHeaders();
        List<TransportHeadersModifier> modifiers = modifiersProvider.getIfAvailable();
        for (TransportHeadersModifier modifier : NullSafe.of(modifiers)) {
            modifier.modify(transportHeaders);
        }
        return transportHeaders;
    }

}
