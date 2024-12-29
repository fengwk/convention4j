package fun.fengwk.convention4j.springboot.starter.xxljob;

import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

/**
 * @author fengwk
 */
@Component
public class DemoJob {

    @XxlJob("demo-exec")
    public void execute() {
        System.out.println("demo");
    }

}
