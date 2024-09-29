package fun.fengwk.convention4j.api.code;

/**
 * 编码消息解析器
 *
 * @author fengwk
 */
public interface CodeMessageResolver {

    /**
     * 获取指定错误码的错误信息
     *
     * @param code 编码
     * @return 错误码信息
     */
    String resolveMessage(Code code);

    /**
     * 格式化消息，如果消息内容中包含${}符号引用的变量将使用ctx进行格式化
     *
     * @param code    编码
     * @param context 格式化使用的上下文
     * @return 错误码信息
     */
    String resolveMessage(Code code, Object context);

}
