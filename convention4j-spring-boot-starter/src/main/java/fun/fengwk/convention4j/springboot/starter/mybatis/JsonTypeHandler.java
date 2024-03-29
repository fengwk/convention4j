package fun.fengwk.convention4j.springboot.starter.mybatis;

import fun.fengwk.convention4j.common.json.JsonUtils;
import fun.fengwk.convention4j.common.reflect.TypeResolver;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 对于简单的JavaBean类型可以直接使用{@code @TypeHandler(JsonTypeHandler.class)}进行注解，
 * 对于复杂泛型类型由于类型擦除和mybatis的一些缺陷，需要声明一个继承{@code JsonTypeHandler}的具体处理器进行处理。
 *
 * <pre>{@code
 *     public static class ListBeanJsonTypeHandler extends JsonTypeHandler<List<Bean>> {}
 *
 *     @TypeHandler(ListBeanJsonTypeHandler.class)
 *     private List<Bean> beanList;
 * }</pre>
 *
 * @author fengwk
 */
public class JsonTypeHandler<T> extends BaseTypeHandler<T> {

    private final Type type;

    public JsonTypeHandler() {
        this.type = new TypeResolver(getClass()).as(JsonTypeHandler.class)
            .asParameterizedType().getActualTypeArguments()[0];
    }

    public JsonTypeHandler(Class<T> javaClass) {
        if (javaClass == Object.class) {
            // 如果mybatis无法解析类型则尝试使用类泛型解析
            this.type = new TypeResolver(getClass()).as(JsonTypeHandler.class)
                .asParameterizedType().getActualTypeArguments()[0];
        } else {
            this.type = javaClass;
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JsonUtils.toJson(parameter));
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String columnValue = rs.getString(columnName);
        return JsonUtils.fromJson(columnValue, type);
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String columnValue = rs.getString(columnIndex);
        return JsonUtils.fromJson(columnValue, type);
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String columnValue = cs.getString(columnIndex);
        return JsonUtils.fromJson(columnValue, type);
    }

}
