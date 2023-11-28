package fun.fengwk.convention4j.springboot.starter.web;

import fun.fengwk.convention4j.api.code.CommonErrorCodes;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.result.Results;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @see org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController
 * @author fengwk
 */
@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class WebErrorController implements ErrorController {
    
    private static final Logger log = LoggerFactory.getLogger(WebErrorController.class);

    @PostConstruct
    public void init() {
        log.info("started {}", getClass().getSimpleName());
    }

    @RequestMapping
    public ResponseEntity<Result<Void>> error(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        fun.fengwk.convention4j.api.code.HttpStatus httpStatus = fun.fengwk.convention4j.api.code.HttpStatus.of(status.value());
        CommonErrorCodes errorCode = CommonErrorCodes.of(httpStatus);
        if (errorCode == null) {
            errorCode = CommonErrorCodes.INTERNAL_SERVER_ERROR;
        }
        Result<Void> result = Results.error(errorCode);
        return new ResponseEntity<>(result, status);
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        }
        catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

}
