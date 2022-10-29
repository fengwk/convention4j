package fun.fengwk.convention4j.springboot.starter.eventbus;

import com.google.common.eventbus.EventBus;

/**
 * 标记接口，继承该接口即可自动集成到EventBus中，在实现类要监听的方法中标记@Subscribe即可进行监听。
 *
 * <pre>{@code
 * @Component
 * public class MyListener implements EventListener {
 *
 *     @Subscribe
 *     public void onIntegerEvent(Integer event) {
 *         System.out.println(event);
 *     }
 *
 *     @Subscribe
 *     public void onMyEvent(MyEvent event) {
 *         System.out.println(event);
 *     }
 *
 * }
 * }</pre>
 *
 * @author fengwk
 */
public interface EventListener {

}
