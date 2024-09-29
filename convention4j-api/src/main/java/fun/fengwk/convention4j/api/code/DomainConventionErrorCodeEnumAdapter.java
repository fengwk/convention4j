package fun.fengwk.convention4j.api.code;

import java.util.Collections;
import java.util.Map;

/**
 * DomainConventionErrorCode的枚举适配器
 *
 * @author fengwk
 */
public interface DomainConventionErrorCodeEnumAdapter extends DomainConventionCodeEnumAdapter, DomainConventionErrorCode {

    @Override
    default Map<String, Object> getErrorContext() {
        return Collections.emptyMap();
    }

}

