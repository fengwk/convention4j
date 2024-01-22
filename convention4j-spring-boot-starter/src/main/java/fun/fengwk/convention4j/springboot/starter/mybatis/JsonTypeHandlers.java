package fun.fengwk.convention4j.springboot.starter.mybatis;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author fengwk
 */
public class JsonTypeHandlers {

    public static class StringListTypeHandler extends JsonTypeHandler<List<String>> {}
    public static class IntegerListTypeHandler extends JsonTypeHandler<List<Integer>> {}
    public static class LongListTypeHandler extends JsonTypeHandler<List<Long>> {}
    public static class BooleanListTypeHandler extends JsonTypeHandler<List<Boolean>> {}
    public static class StringSetTypeHandler extends JsonTypeHandler<Set<String>> {}
    public static class IntegerSetTypeHandler extends JsonTypeHandler<Set<Integer>> {}
    public static class LongSetTypeHandler extends JsonTypeHandler<Set<Long>> {}
    public static class BooleanSetTypeHandler extends JsonTypeHandler<Set<Boolean>> {}
    public static class StringCollectionTypeHandler extends JsonTypeHandler<Collection<String>> {}
    public static class IntegerCollectionTypeHandler extends JsonTypeHandler<Collection<Integer>> {}
    public static class LongCollectionTypeHandler extends JsonTypeHandler<Collection<Long>> {}
    public static class BooleanCollectionTypeHandler extends JsonTypeHandler<Collection<Boolean>> {}

    private JsonTypeHandlers() {}

}
