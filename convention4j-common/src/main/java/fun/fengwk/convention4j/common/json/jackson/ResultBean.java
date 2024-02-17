package fun.fengwk.convention4j.common.json.jackson;

import fun.fengwk.convention4j.api.result.Errors;
import lombok.Data;

/**
 * @author fengwk
 */
@Data
public class ResultBean<T> {

    private int status;
    private String message;
    private T data;
    private Errors errors;

}
