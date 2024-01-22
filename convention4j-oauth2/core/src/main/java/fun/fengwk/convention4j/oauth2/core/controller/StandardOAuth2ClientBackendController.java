package fun.fengwk.convention4j.oauth2.core.controller;

import fun.fengwk.convention4j.api.page.Page;
import fun.fengwk.convention4j.api.page.PageQuery;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.gson.GsonUtils;
import fun.fengwk.convention4j.common.result.Results;
import fun.fengwk.convention4j.oauth2.core.service.StandardOAuth2ClientBackendService;
import fun.fengwk.convention4j.oauth2.share.model.StandardOAuth2ClientCreateDTO;
import fun.fengwk.convention4j.oauth2.share.model.StandardOAuth2ClientDTO;
import fun.fengwk.convention4j.oauth2.share.model.StandardOAuth2ClientUpdateDTO;
import fun.fengwk.convention4j.springboot.starter.scan.ExcludeComponent;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;

/**
 * @author fengwk
 */
@AllArgsConstructor
@ExcludeComponent
@RequestMapping("${convention.oauth2.client-rest-prefix:}")
@RestController
public class StandardOAuth2ClientBackendController<
    CLIENT_DTO extends StandardOAuth2ClientDTO,
    CLIENT_CREATE_DTO extends StandardOAuth2ClientCreateDTO,
    CLIENT_UPDATE_DTO extends StandardOAuth2ClientUpdateDTO> {

    protected final StandardOAuth2ClientBackendService<CLIENT_DTO, CLIENT_CREATE_DTO, CLIENT_UPDATE_DTO> standardOAuth2ClientBackendService;
    protected final Type clientCreateDTOType;
    protected final Type clientUpdateDTOType;

    /**
     * 创建客户端
     *
     * @param createDTOJson 客户端创建对象
     * @return 客户端后台对象
     */
    @PostMapping
    public Result<CLIENT_DTO> createClient(@RequestBody String createDTOJson) {
        CLIENT_CREATE_DTO createDTO = GsonUtils.fromJson(createDTOJson, clientCreateDTOType);
        CLIENT_DTO clientDTO = standardOAuth2ClientBackendService.createClient(createDTO);
        return Results.created(clientDTO);
    }

    /**
     * 更新客户端（全量更新）
     *
     * @param clientId  客户端id
     * @param updateDTOJson 客户端创建对象
     * @return 客户端后台对象
     */
    @PutMapping("/{clientId}")
    public Result<CLIENT_DTO> updateClient(@PathVariable("clientId") String clientId,
                                           @RequestBody String updateDTOJson) {
        CLIENT_UPDATE_DTO updateDTO = GsonUtils.fromJson(updateDTOJson, clientUpdateDTOType);
        updateDTO.setClientId(clientId);
        CLIENT_DTO clientDTO = standardOAuth2ClientBackendService.updateClient(updateDTO);
        return Results.ok(clientDTO);
    }

    /**
     * 更新clientId
     *
     * @param clientId    clientId
     * @param newClientId 新clientId
     */
    @PatchMapping("/{clientId}/client-id")
    public Result<Void> updateClientId(@PathVariable("clientId") String clientId,
                                       @RequestParam("newClientId") String newClientId) {
        standardOAuth2ClientBackendService.updateClientId(clientId, newClientId);
        return Results.ok();
    }

    /**
     * 删除客户端
     *
     * @param clientId 客户端id
     */
    @DeleteMapping("/{clientId}")
    public Result<Void> removeClient(@PathVariable("clientId") String clientId) {
        standardOAuth2ClientBackendService.removeClient(clientId);
        return Results.noContent();
    }

    /**
     * 启用客户端
     *
     * @param clientId 客户端id
     */
    @PatchMapping("/{clientId}/enable")
    public Result<Void> enableClient(@PathVariable("clientId") String clientId) {
        standardOAuth2ClientBackendService.enableClient(clientId);
        return Results.ok();
    }

    /**
     * 禁用客户端
     *
     * @param clientId 客户端id
     */
    @PatchMapping("/{clientId}/disable")
    public Result<Void> disableClient(@PathVariable("clientId") String clientId) {
        standardOAuth2ClientBackendService.disableClient(clientId);
        return Results.ok();
    }

    /**
     * 分页查询客户端
     *
     * @param pageNumber 页码，默认1
     * @param pageSize   页尺寸，默认20
     * @param keyword    搜索关键字，客户端id前缀、客户端名前缀
     * @return 客户端分页
     */
    @GetMapping("/page")
    public Result<Page<CLIENT_DTO>> pageClient(
        @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
        @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,
        @RequestParam(value = "keyword", required = false) String keyword) {
        PageQuery pageQuery = new PageQuery(pageNumber, pageSize);
        Page<CLIENT_DTO> page = standardOAuth2ClientBackendService.pageClient(pageQuery, keyword);
        return Results.ok(page);
    }

}
