package fun.fengwk.convention4j.springboot.starter.cache.meta;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.function.Function;

/**
 * @author fengwk
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public class MethodKeyMeta extends KeyMeta {

    private final int parameterIndex;
    private final boolean multi;

    public MethodKeyMeta(
        boolean id, String name, Function<Object, Object> valueGetter,
        int parameterIndex, boolean multi) {
        super(id, name, valueGetter);
        this.parameterIndex = parameterIndex;
        this.multi = multi;
    }

    public Object selectParameter(Object...args) {
        return args == null || args.length == 0 ? null : args[parameterIndex];
    }

}
