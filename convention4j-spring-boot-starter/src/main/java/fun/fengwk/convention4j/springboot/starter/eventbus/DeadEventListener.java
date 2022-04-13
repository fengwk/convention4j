package fun.fengwk.convention4j.springboot.starter.eventbus;

import com.google.common.eventbus.DeadEvent;

/**
 * 死信监听器。
 *
 * @author fengwk
 */
public interface DeadEventListener {

    /**
     * 当出现死信时将回调该方法。
     *
     * @param deadEvent
     */
    void onEvent(DeadEvent deadEvent);

}
