package fun.fengwk.convention4j.common.sql.dynamic.node;

import ognl.Ognl;
import ognl.OgnlException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fengwk
 */
public class ForeachNode extends AbstractContainerNode {

    private final String item;
    private final String index;
    private final String collection;
    private final String open;
    private final String separator;
    private final String close;

    public ForeachNode(String item, String index, String collection, String open, String separator, String close) {
        this.item = item;
        this.index = index;
        this.collection = collection;
        this.open = open;
        this.separator = separator;
        this.close = close;
    }

    @Override
    public boolean interpret(InterpretContext ctx) throws InterpretException {
        Object collectionObj;
        try {
            collectionObj = Ognl.getValue(collection, ctx.getVarMap());
        } catch (OgnlException e) {
            throw new InterpretException(e);
        }

        if (collectionObj == null) {
            throw new InterpretException("collection cannot be null");
        }

        if (collectionObj.getClass().isArray()) {
            // 将数组转换为统一的集合外观便于后续处理
            List<Object> list = new ArrayList<>();
            int len = Array.getLength(collectionObj);
            for (int i = 0; i < len; i++) {
                list.add(Array.get(collectionObj, i));
            }
            collectionObj = list;
        }

        if (!(collectionObj instanceof Collection)) {
            throw new InterpretException(String.format("collection '%s' is not a Collection", collectionObj));
        }

        final Collection<?> finalCollectionObj = (Collection<?>) collectionObj;
        combine(ctx, (combineSqlBuilder, combineParamList) -> {
            int idx = 0;
            for (Object itemObj : finalCollectionObj) {
                // 替换变量表为局部变量表，局部变量表中将包含本次循环需要用到的item和index变量
                Map<String, Object> storeVarMap = ctx.getVarMap();
                Map<String, Object> localVarMap = new HashMap<>(storeVarMap);
                localVarMap.put(item, itemObj);
                localVarMap.put(index, idx);
                ctx.setVarMap(localVarMap);

                // 使用替换局部变量表后的上下文对所有孩子节点进行解释
                interpretChildren(ctx);

                // do combine
                if (ctx.getSql() != null) {
                    if (idx == 0) {
                        combineSqlBuilder.append(' ');
                    } else if (separator != null) {
                        combineSqlBuilder.append(separator);
                    }
                    combineSqlBuilder.append(ctx.getSql());
                }
                if (ctx.getParamList() != null) {
                    combineParamList.addAll(ctx.getParamList());
                }

                // 恢复变量表
                ctx.setVarMap(storeVarMap);

                idx++;
            }
        });

        if (open != null) {
            ctx.setSql(open + ctx.getSql());
        }
        if (close != null) {
            ctx.setSql(ctx.getSql() + close);
        }

        return true;
    }

}
