package fun.fengwk.convention4j.tracer.reactor.aspect;

import lombok.extern.slf4j.Slf4j;
import reactor.core.CoreSubscriber;
import reactor.util.context.Context;

/**
 * @author fengwk
 */
@Slf4j
public class CoreSubscriberAspectAdapter<T, S extends CoreSubscriber<T>>
        extends SubscriberAspectAdapter<T, S> implements CoreSubscriber<T> {

    private final Context context;

    public CoreSubscriberAspectAdapter(SubscriberAspect aspect, S actual) {
        super(aspect, actual);
        this.context = aspect.writeContext(actual.currentContext());
    }

    @Override
    protected Context internalCurrentContext() {
        return currentContext();
    }

    @Override
    public Context currentContext() {
        return context;
    }

}

