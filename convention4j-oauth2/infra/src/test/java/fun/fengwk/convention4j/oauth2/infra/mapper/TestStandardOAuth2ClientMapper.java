package fun.fengwk.convention4j.oauth2.infra.mapper;

import fun.fengwk.automapper.annotation.AutoMapper;
import fun.fengwk.convention4j.oauth2.infra.model.StandardOAuth2ClientDO;
import fun.fengwk.convention4j.springboot.starter.cache.mapper.CacheableMapper;

/**
 * @author fengwk
 */
@AutoMapper(tableName = "client")
public interface TestStandardOAuth2ClientMapper extends StandardOAuth2ClientMapper<StandardOAuth2ClientDO>,
    CacheableMapper<StandardOAuth2ClientDO, Long> {

}
