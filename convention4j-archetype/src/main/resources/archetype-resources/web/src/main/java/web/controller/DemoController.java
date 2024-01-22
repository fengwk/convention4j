#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.web.controller;

import fun.fengwk.convention4j.api.page.Page;
import fun.fengwk.convention4j.api.page.PageQuery;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.result.Results;
import ${package}.core.service.DemoService;
import ${package}.share.model.DemoCreateDTO;
import ${package}.share.model.DemoDTO;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author fengwk
 */
@AllArgsConstructor
@RequestMapping("/api/demo")
@RestController
public class DemoController {

    private final DemoService demoService;

    @PostMapping
    public Result<DemoDTO> createDemo(@RequestBody DemoCreateDTO createDTO) {
        DemoDTO demo = demoService.createDemo(createDTO);
        return Results.created(demo);
    }

    @DeleteMapping("/{id}")
    public Result<Void> removeDemo(@PathVariable("id") long id) {
        demoService.removeDemo(id);
        return Results.noContent();
    }

    @GetMapping
    public Result<Page<DemoDTO>> pageDemo(@RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                                          @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        PageQuery pageQuery = new PageQuery(pageNumber, pageSize);
        Page<DemoDTO> page = demoService.pageDemo(pageQuery);
        return Results.ok(page);
    }

}
