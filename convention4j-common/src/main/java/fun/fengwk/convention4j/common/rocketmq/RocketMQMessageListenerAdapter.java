package fun.fengwk.convention4j.common.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.MessageListener;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @see RocketMQMessageListener
 * @author fengwk
 */
@Slf4j
public class RocketMQMessageListenerAdapter implements MessageListener {

    private final Object bean;
    private final Method method;
    private final Function<MessageView, Object> inputAdapter;
    private final BiFunction<MessageView, Supplier<Object>, ConsumeResult> outputAdapter;

    public RocketMQMessageListenerAdapter(Object bean, Method method) {
        this.bean = Objects.requireNonNull(bean);
        this.method = Objects.requireNonNull(method);
        ReflectionUtils.makeAccessible(method);

        if (method.getParameterCount() != 1) {
            throw new IllegalArgumentException(String.format("The method[%s] annotated with " +
                "@RocketMQMessageListener must contain input parameters that comply with the specification", method.getName()));
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        Class<?> inputClass = parameterTypes[0];
        if (MessageView.class.isAssignableFrom(inputClass)) {
            this.inputAdapter = mv -> mv;
        } else if (CharSequence.class.isAssignableFrom(inputClass)) {
            this.inputAdapter = RocketMQUtils::getStringBody;
        } else {
            Type[] genericParameterTypes = method.getGenericParameterTypes();
            Type inputType = genericParameterTypes[0];
            this.inputAdapter = mv -> {
                try {
                    return RocketMQUtils.getObjectBody(mv, inputType);
                } catch (Exception ex) {
                    log.error("convert rocket mq message error, messageView: {}, method: {}, bodyStr: {}",
                        mv, method, RocketMQUtils.getStringBody(mv), ex);
                    return null;
                }
            };
        }

        Class<?> returnType = method.getReturnType();
        if (returnType == ConsumeResult.class) {
            this.outputAdapter = (mv, exec) -> {
                try {
                    return (ConsumeResult) exec.get();
                } catch (Exception ex) {
                    log.error("execute rocket mq message listener error, messageView: {}, method: {}", mv, method, ex);
                    return ConsumeResult.FAILURE;
                }
            };
        } else if (returnType == boolean.class || returnType == Boolean.class) {
            this.outputAdapter = (mv, exec) -> {
                try {
                    return Objects.equals(exec.get(), Boolean.TRUE) ? ConsumeResult.SUCCESS : ConsumeResult.FAILURE;
                } catch (Exception ex) {
                    log.error("execute rocket mq message listener error, messageView: {}, method: {}", mv, method, ex);
                    return ConsumeResult.FAILURE;
                }
            };
        } else {
            this.outputAdapter = (mv, exec) -> {
                try {
                    exec.get();
                    return ConsumeResult.SUCCESS;
                } catch (Exception ex) {
                    log.error("execute rocket mq message listener error, messageView: {}, method: {}", mv, method, ex);
                    return ConsumeResult.FAILURE;
                }
            };
        }
    }

    @Override
    public ConsumeResult consume(MessageView messageView) {
        Object input = inputAdapter.apply(messageView);
        return outputAdapter.apply(messageView, () -> ReflectionUtils.invokeMethod(method, bean, input));
    }

}
