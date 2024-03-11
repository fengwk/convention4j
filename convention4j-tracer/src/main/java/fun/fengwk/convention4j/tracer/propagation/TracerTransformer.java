package fun.fengwk.convention4j.tracer.propagation;

import fun.fengwk.convention4j.common.util.LazyServiceLoader;
import fun.fengwk.convention4j.tracer.propagation.extract.Extract;
import fun.fengwk.convention4j.tracer.propagation.inject.Inject;
import io.opentracing.SpanContext;
import io.opentracing.propagation.Format;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fengwk
 */
@Slf4j
public class TracerTransformer {

    private final Map<Format<?>, Inject> injectRegistry;
    private final Map<Format<?>, Extract> extractRegistry;

    public TracerTransformer() {
        this.injectRegistry = loadRegistry(Inject.class);
        this.extractRegistry = loadRegistry(Extract.class);
    }

    /**
     * @see io.opentracing.Tracer#inject(SpanContext, Format, Object)
     */
    public <C> void inject(SpanContext spanContext, Format<C> format, C carrier) {
        Inject<C> inject = (Inject<C>) injectRegistry.get(format);
        if (inject == null) {
            log.warn("Inject not found, format: {}", format);
            return;
        }
        inject.inject(spanContext, carrier);
    }

    /**
     * @see io.opentracing.Tracer#extract(Format, Object)
     */
    public <C> SpanContext extract(Format<C> format, C carrier) {
        Extract<C> extract = (Extract<C>) extractRegistry.get(format);
        if (extract == null) {
            log.warn("Extract not found, format: {}", format);
            return null;
        }
        return extract.extract(carrier);
    }

    private <T extends TransformerSupport<?>> Map<Format<?>, T> loadRegistry(Class<T> clazz) {
        List<T> services = LazyServiceLoader.loadServiceIgnoreLoadFailed(clazz);
        Map<Format<?>, T> registry = new HashMap<>();
        for (T service : services) {
            registry.put(service.format(), service);
        }
        return registry;
    }

}
