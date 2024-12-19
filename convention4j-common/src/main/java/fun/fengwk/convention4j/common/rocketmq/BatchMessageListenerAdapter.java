package fun.fengwk.convention4j.common.rocketmq;

import fun.fengwk.convention4j.common.runtimex.RuntimeExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author fengwk
 */
@Slf4j
public class BatchMessageListenerAdapter implements BatchMessageListener {

    private final Object bean;
    private final Method method;

    public BatchMessageListenerAdapter(Object bean, Method method) {
        this.bean = Objects.requireNonNull(bean);
        this.method = Objects.requireNonNull(method);
        ReflectionUtils.makeAccessible(method);

        if (!isValidInputType(method)) {
            throw new IllegalArgumentException(String.format("The method[%s] annotated with " +
                "@RocketMQBatchMessageListener must only contain List<MessageView> parameters", method.getName()));
        }

        if (!isValidReturnType(method)) {
            throw new IllegalArgumentException(String.format("The method[%s] annotated with " +
                "@RocketMQBatchMessageListener must return successful Collection<MessageView>", method.getName()));
        }
    }

    @Override
    public Collection<MessageView> consume(List<MessageView> messageViewList) {
        try {
            return (Collection<MessageView>) method.invoke(bean, messageViewList);
        } catch (Exception ex) {
            throw new RuntimeExecutionException(ex);
        }
    }

    private boolean isValidInputType(Method method) {
        if (method.getParameterCount() != 1) {
            return false;
        }

        Class<?>[] parameterTypes = method.getParameterTypes();
        Class<?> inputClass = parameterTypes[0];
        if (inputClass != List.class) {
            return false;
        }

        Type[] genericParameterTypes = method.getGenericParameterTypes();
        Type inputType = genericParameterTypes[0];
        if (!(inputType instanceof ParameterizedType pt)) {
            return false;
        }
        Type[] ata = pt.getActualTypeArguments();
        if (ata.length != 1) {
            return false;
        }
        return ata[0] == MessageView.class;
    }

    private boolean isValidReturnType(Method method) {
        Class<?> returnClass = method.getReturnType();
        if (!Collection.class.isAssignableFrom(returnClass)) {
            return false;
        }

        Type returnType = method.getGenericReturnType();
        if (!(returnType instanceof ParameterizedType pt)) {
            return false;
        }
        Type[] ata = pt.getActualTypeArguments();
        if (ata.length != 1) {
            return false;
        }
        return ata[0] == MessageView.class;
    }

}
