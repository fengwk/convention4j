package fun.fengwk.convention4j.oauth2.core.service;

import fun.fengwk.convention4j.api.page.Page;
import fun.fengwk.convention4j.api.page.PageQuery;
import fun.fengwk.convention4j.oauth2.share.model.StandardOAuth2ClientCreateDTO;
import fun.fengwk.convention4j.oauth2.share.model.StandardOAuth2ClientDTO;
import fun.fengwk.convention4j.oauth2.share.model.StandardOAuth2ClientUpdateDTO;

/**
 * @author fengwk
 */
public interface StandardOAuth2ClientBackendService<
    CLIENT_DTO extends StandardOAuth2ClientDTO,
    CLIENT_CREATE_DTO extends StandardOAuth2ClientCreateDTO,
    CLIENT_UPDATE_DTO extends StandardOAuth2ClientUpdateDTO> {

    /**
     * 创建客户端
     *
     * @param createDTO 客户端创建对象
     * @return 客户端后台对象
     */
    CLIENT_DTO createClient(CLIENT_CREATE_DTO createDTO);

    /**
     * 更新客户端（全量更新）
     *
     * @param updateDTO 客户端创建对象
     * @return 客户端后台对象
     */
    CLIENT_DTO updateClient(CLIENT_UPDATE_DTO updateDTO);

    /**
     * 更新clientId
     *
     * @param clientId    clientId
     * @param newClientId 新clientId
     */
    void updateClientId(String clientId, String newClientId);

    /**
     * 删除客户端
     *
     * @param clientId 客户端id
     */
    void removeClient(String clientId);

    /**
     * 启用客户端
     *
     * @param clientId 客户端id
     */
    void enableClient(String clientId);

    /**
     * 禁用客户端
     *
     * @param clientId 客户端id
     */
    void disableClient(String clientId);

    /**
     * 分页查询客户端
     *
     * @param pageQuery 分页查询器
     * @param keyword   搜索关键字，客户端id前缀、客户端名前缀
     * @return 客户端分页
     */
    Page<CLIENT_DTO> pageClient(PageQuery pageQuery, String keyword);

}
