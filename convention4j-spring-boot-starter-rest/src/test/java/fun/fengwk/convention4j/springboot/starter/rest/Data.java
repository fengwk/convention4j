package fun.fengwk.convention4j.springboot.starter.rest;

import javax.validation.constraints.Max;

/**
 * @author fengwk
 */
public class Data {

    @Max(15)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
