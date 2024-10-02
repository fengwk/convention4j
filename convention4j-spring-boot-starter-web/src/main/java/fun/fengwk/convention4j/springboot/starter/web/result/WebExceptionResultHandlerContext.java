package fun.fengwk.convention4j.springboot.starter.web.result;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.WebRequest;

/**
 * @author fengwk
 */
@Data
public class WebExceptionResultHandlerContext {

    private HttpServletRequest request;
    private WebRequest webRequest;

    // 对响应结果预设的headers
    private HttpHeaders responseHeaders;

}
