package fun.fengwk.convention4j.api.code;

/**
 * DomainConventionCode的枚举适配器
 *
 * @author fengwk
 */
public interface DomainConventionCodeEnumAdapter extends DomainConventionCode {

    HttpStatus getHttpStatus();

    @Override
    default int getStatus() {
        return getHttpStatus().getStatus();
    }

    @Override
    default String getMessage() {
        return getHttpStatus().getMessage();
    }

    @Override
    default String getDomainCode() {
        return name();
    }

    /**
     * @see Enum#name()
     */
    String name();

}
