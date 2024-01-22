package fun.fengwk.convention4j.oauth2.infra.mapper;

import fun.fengwk.automapper.annotation.Selective;
import fun.fengwk.convention4j.oauth2.infra.model.StandardOAuth2ClientDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author fengwk
 */
public interface StandardOAuth2ClientMapper<CLIENT_DO extends StandardOAuth2ClientDO> {

    void createTableIfNotExists();

    int insertSelective(CLIENT_DO clientDO);

    int updateByClientIdSelective(CLIENT_DO clientDO);

    int updateClientId(@Param("clientId") String clientId, @Param("newClientId") String newClientId);

    int deleteByClientId(String clientId);

    int countByClientId(String clientId);

    CLIENT_DO getByClientId(String clientId);

    long countByClientIdStartingWithOrNameStartingWith(
        @Selective @Param("clientId") String clientIdPrefix, @Selective @Param("name") String namePrefix);

    List<CLIENT_DO> pageByClientIdStartingWithOrNameStartingWithOrderByCreateTimeDesc(
        @Param("offset") long offset, @Param("limit") int limit,
        @Selective @Param("clientId") String clientIdPrefix, @Selective @Param("name") String namePrefix);

}
