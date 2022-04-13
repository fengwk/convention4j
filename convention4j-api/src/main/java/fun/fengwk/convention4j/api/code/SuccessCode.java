package fun.fengwk.convention4j.api.code;

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
