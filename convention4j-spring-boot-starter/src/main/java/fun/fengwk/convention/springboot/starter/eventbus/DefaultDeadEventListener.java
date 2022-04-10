package fun.fengwk.convention.springboot.starter.eventbus;

import com.google.common.eventbus.DeadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fengwk
 */
public class DefaultDeadEventListener implements DeadEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDeadEventListener.class);

    @Override
    public void onEvent(DeadEvent deadEvent) {
        LOG.warn("discover dead event '{}'", deadEvent);
    }
}
