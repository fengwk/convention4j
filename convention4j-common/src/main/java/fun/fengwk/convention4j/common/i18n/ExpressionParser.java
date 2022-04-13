package fun.fengwk.convention4j.common.i18n;

import ognl.Ognl;
import ognl.OgnlException;

import java.util.LinkedList;
import java.util.Map;

/**
 * 
 * @author fengwk
 */
public class ExpressionParser {
    
    private static final String OPEN = "${";
    private static final char CLOSE = '}';
    
    private ExpressionParser() {}
    
    // 解析str[lo, hi)的表达式内容并返回
    public static String parse(String str, Map<String, ?> ctx) throws OgnlException {
        // stack元素为出现OPEN之后的首个位置。
        LinkedList<Integer> stack = new LinkedList<>();
        
        // 使用栈顺序为每个${...}区间维护一个StringBuilder
        LinkedList<StringBuilder> sbStack = new LinkedList<>();
        sbStack.push(new StringBuilder());
       
        for (int i = 0; i < str.length();) {
            char c = str.charAt(i);
            if (c == OPEN.charAt(0) && i+1 < str.length() && str.charAt(i+1) == OPEN.charAt(1)) {
                i+=2;
                stack.push(i);
                sbStack.push(new StringBuilder());
            }
            
            else if (c == CLOSE) {
                if (stack.isEmpty()) {
                    throw new IllegalArgumentException(
                            String.format("Expression error, cannot found %s for % in pos %d", OPEN, CLOSE, i));
                }
                
                String expStr = sbStack.pop().toString();
                String resultStr = doParse(expStr, ctx);
                sbStack.peek().append(resultStr);
                i++;
            }
            
            else {
                sbStack.peek().append(c);
                i++;
            }
        }
        
        return sbStack.peek().toString();
    }
    
    private static String doParse(String str, Map<String, ?> ctx) throws OgnlException {
        return String.valueOf(Ognl.getValue(str, ctx));
    }
    
}
