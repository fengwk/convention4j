package fun.fengwk.convention4j.springboot.starter.mybatis;

import fun.fengwk.convention4j.common.gson.GsonUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author fengwk
 */
public class JsonHandler extends BaseTypeHandler<Object> {

    private final Class<?> javaClass;

    public JsonHandler(Class<?> javaClass) {
        this.javaClass = javaClass;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, GsonUtils.toJson(parameter));
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String columnValue = rs.getString(columnName);
        return GsonUtils.fromJson(columnValue, javaClass);
    }

    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String columnValue = rs.getString(columnIndex);
        return GsonUtils.fromJson(columnValue, javaClass);
    }

    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String columnValue = cs.getString(columnIndex);
        return GsonUtils.fromJson(columnValue, javaClass);
    }

}
