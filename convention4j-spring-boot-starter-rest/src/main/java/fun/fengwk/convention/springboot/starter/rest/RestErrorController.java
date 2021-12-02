package fun.fengwk.convention.springboot.starter.rest;

import fun.fengwk.convention.api.code.ErrorCodeFactory;
import fun.fengwk.convention.api.result.Result;
import fun.fengwk.convention.api.result.Results;
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

import static fun.fengwk.convention.api.code.ErrorCodes.FORBIDDEN;
import static fun.fengwk.convention.api.code.ErrorCodes.ILLEGAL_ARGUMENT;
import static fun.fengwk.convention.api.code.ErrorCodes.ILLEGAL_STATE;
import static fun.fengwk.convention.api.code.ErrorCodes.RESOURCE_NOT_FOUND;
import static fun.fengwk.convention.api.code.ErrorCodes.UNAUTHORIZED;

/**
 * @see org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController
 * @author fengwk
 */
@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class RestErrorController implements ErrorController {
    
    private static final Logger LOG = LoggerFactory.getLogger(RestErrorController.class);
    
    private final ErrorCodeFactory codeFactory;
    
    public RestErrorController(ErrorCodeFactory codeFactory) {
        this.codeFactory = codeFactory;
    }

    @PostConstruct
    public void init() {
        LOG.info("Started {}", getClass().getSimpleName());
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

}
