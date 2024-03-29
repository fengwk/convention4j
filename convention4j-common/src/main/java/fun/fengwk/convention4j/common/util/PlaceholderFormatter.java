package fun.fengwk.convention4j.common.util;


import fun.fengwk.convention4j.common.lang.StringUtils;

/**
 * {@link PlaceholderFormatter}提供了占位符字符串的格式化功能，
 * 例如下边的例子可以将输入的{@code hello, {}}格式化为{@code hello, fengwk}。
 *
 * <pre>{@code
 *     PlaceholderFormatter pf = new PlaceholderFormatter("{}");
 *     pf.format("hello, {}", "fengwk");
 * }</pre>
 * 
 * @author fengwk
 */
public class PlaceholderFormatter {

    private final String placeholder;

    /**
     *
     * @param placeholder not empty
     */
    public PlaceholderFormatter(String placeholder) {
        if (StringUtils.isEmpty(placeholder)) {
            throw new IllegalArgumentException("placeholder cannot be empty");
        }

        this.placeholder = placeholder;
    }
    
    public String format(String format, Object... args) {
        if (format == null || args == null || args.length == 0) {
            return format;
        }
        
        int offset = 0;
        int arrIdx = 0;
        int i;
        StringBuilder sb = new StringBuilder();
        while ((i = format.indexOf(placeholder, offset)) != -1 && arrIdx < args.length) {
            sb.append(format.substring(offset, i));
            sb.append(args[arrIdx++]);
            offset = i + placeholder.length();
        }
        if (offset < format.length()) {
            sb.append(format.substring(offset));
        }
        
        return sb.toString();
    }
    
}
