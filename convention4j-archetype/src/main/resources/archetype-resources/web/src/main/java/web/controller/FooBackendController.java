#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.web.controller;

import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.result.Results;
import ${package}.app.service.FooBackendService;
import ${package}.share.model.FooCreateDTO;
import ${package}.share.model.FooDTO;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author fengwk
 */
@AllArgsConstructor
@RestController
public class FooBackendController {

    private final FooBackendService fooBackendService;

    @PostMapping("/foo")
    public Result<String> createFoo(@RequestBody FooCreateDTO createDTO) {
        String fooId = fooBackendService.createFoo(createDTO);
        return Results.created(fooId);
    }

    @GetMapping("/foo/{fooId}")
    public Result<FooDTO> getByFooId(@PathVariable("fooId") String fooId) {
        FooDTO fooDTO = fooBackendService.getByFooId(fooId);
        return Results.ok(fooDTO);
    }

}
