package fun.fengwk.convention4j.springboot.starter.eventbus;

import com.google.common.eventbus.DeadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认的死信监听器，将在收到死信时打印warn日志。
 *
 * @author fengwk
 */
public class DefaultDeadEventListener implements DeadEventListener {

    private static final Logger log = LoggerFactory.getLogger(DefaultDeadEventListener.class);

    @Override
    public void onEvent(DeadEvent deadEvent) {
        log.warn("discover dead event '{}'", deadEvent);
    }

}
