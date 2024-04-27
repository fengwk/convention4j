package fun.fengwk.convention4j.spring.cloud.starter;

import fun.fengwk.convention4j.spring.cloud.starter.feign.TestFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fengwk
 */
@AllArgsConstructor
@RestController
public class TestController {

    private final TestFeignClient testFeignClient;

    @GetMapping("/api/baidu")
    public String getBaidu() {
        return testFeignClient.get();
    }

}
