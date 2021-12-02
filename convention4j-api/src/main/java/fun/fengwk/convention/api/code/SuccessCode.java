package fun.fengwk.convention.api.code;

/**
 * 成功码。
 * 
 * @author fengwk
 */
public enum SuccessCode implements Code {
    
    INSTANCE;
    
    private static final String SUCCESS_CODE = "0";

    @Override
    public String getCode() {
        return SUCCESS_CODE;
    }
    
}
