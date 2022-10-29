package fun.fengwk.convention4j.springboot.starter.eventbus;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;

/**
 * 死信监听器，将该接口的实现类注入Spring容器可以替代{@link DeadEventListener}。
 *
 * @author fengwk
 */
public interface DeadEventListener {

    /**
     * 出现死信时将回调该方法。
     *
     * @param deadEvent
     */
    @Subscribe
    void onEvent(DeadEvent deadEvent);

}
