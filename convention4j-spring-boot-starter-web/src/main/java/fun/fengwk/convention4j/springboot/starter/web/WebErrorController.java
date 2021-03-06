package fun.fengwk.convention4j.springboot.starter.web;

import fun.fengwk.convention4j.common.code.ErrorCodeFactory;
import fun.fengwk.convention4j.common.result.Result;
import fun.fengwk.convention4j.common.result.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import static fun.fengwk.convention4j.common.code.CommonCodeTable.FORBIDDEN;
import static fun.fengwk.convention4j.common.code.CommonCodeTable.ILLEGAL_ARGUMENT;
import static fun.fengwk.convention4j.common.code.CommonCodeTable.ILLEGAL_STATE;
import static fun.fengwk.convention4j.common.code.CommonCodeTable.RESOURCE_NOT_FOUND;
import static fun.fengwk.convention4j.common.code.CommonCodeTable.UNAUTHORIZED;

/**
 * @see org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController
 * @author fengwk
 */
@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class WebErrorController implements ErrorController {
    
    private static final Logger log = LoggerFactory.getLogger(WebErrorController.class);
    
    private final ErrorCodeFactory codeFactory;
    
    public WebErrorController(ErrorCodeFactory codeFactory) {
        this.codeFactory = codeFactory;
    }

    @PostConstruct
    public void init() {
        log.info("started {}", getClass().getSimpleName());
    }

    @RequestMapping
    public ResponseEntity<Result<Void>> error(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        Result<Void> result;
        if (status == HttpStatus.NOT_FOUND) {
            result = Results.of(codeFactory.create(RESOURCE_NOT_FOUND));
        } else if (status == HttpStatus.UNAUTHORIZED) {
            result = Results.of(codeFactory.create(UNAUTHORIZED));
        } else if (status == HttpStatus.FORBIDDEN) {
            result = Results.of(codeFactory.create(FORBIDDEN));
        } else if (status.is4xxClientError()) {
            result = Results.of(codeFactory.create(ILLEGAL_ARGUMENT));
        } else {
            result = Results.of(codeFactory.create(ILLEGAL_STATE));
        }
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

    @Override
    public String getErrorPath() {
        return null;
    }

}
