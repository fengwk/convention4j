package fun.fengwk.convention4j.common.sql.dynamic.node;

import fun.fengwk.convention4j.common.util.NullSafe;

import java.util.List;
import java.util.Map;

/**
 * 解释上下文。
 *
 * @author fengwk
 */
public class InterpretContext {

    private Map<String, Object> varMap;

    private String sql;
    private List<Object> paramList;

    public InterpretContext(Map<String, Object> varMap) {
        this.varMap = NullSafe.of(varMap);
    }

    public Map<String, Object> getVarMap() {
        return varMap;
    }

    public void setVarMap(Map<String, Object> varMap) {
        this.varMap = varMap;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Object> getParamList() {
        return paramList;
    }

    public void setParamList(List<Object> paramList) {
        this.paramList = paramList;
    }

}
