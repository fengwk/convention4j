package fun.fengwk.convention4j.common.json.jackson;

import lombok.Data;

import java.util.Map;

/**
 * @author fengwk
 */
@Data
public class ResultBean<T> {

    private int status;
    private String code;
    private String message;
    private T data;
    private Map<String, Object> errors;

}
