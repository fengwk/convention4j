package fun.fengwk.convention.api.code;

/**
 * 编码生产工厂。
 * 
 * @author fengwk
 */
public interface ErrorCodeFactory {

    /**
     * 指定编码与默认消息生产错误码。
     * 
     * @param code
     * @return
     */
    ErrorCode create(String code);
    
    /**
     * 指定编码与消息生产错误码。
     * 
     * @param code
     * @param message
     * @return
     */
    ErrorCode create(String code, String message);
    
}
