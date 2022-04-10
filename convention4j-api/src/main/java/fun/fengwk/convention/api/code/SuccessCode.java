package fun.fengwk.convention.api.code;

/**
 * 成功码。
 * 
 * @author fengwk
 */
public enum SuccessCode implements Code {
    
    INSTANCE;

    @Override
    public String getCode() {
        return CommonCodeTable.SUCCESS.getCode();
    }
    
}
