package fun.fengwk.convention4j.spring.cloud.starter.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author fengwk
 */
@FeignClient(value = "baidu", url = "https://baidu.com")
public interface TestFeignClient {

    @GetMapping
    String get();

}
