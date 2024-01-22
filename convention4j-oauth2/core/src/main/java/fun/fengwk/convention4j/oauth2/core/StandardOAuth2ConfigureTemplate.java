package fun.fengwk.convention4j.oauth2.core;

import fun.fengwk.convention4j.common.reflect.TypeResolver;
import fun.fengwk.convention4j.oauth2.core.controller.StandardOAuth2ClientBackendController;
import fun.fengwk.convention4j.oauth2.core.model.StandardOAuth2Client;
import fun.fengwk.convention4j.oauth2.core.service.StandardOAuth2ClientBackendService;
import fun.fengwk.convention4j.oauth2.share.model.StandardOAuth2ClientCreateDTO;
import fun.fengwk.convention4j.oauth2.share.model.StandardOAuth2ClientDTO;
import fun.fengwk.convention4j.oauth2.share.model.StandardOAuth2ClientUpdateDTO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @author fengwk
 */
public abstract class StandardOAuth2ConfigureTemplate<
    SUBJECT,
    CERTIFICATE,
    CLIENT extends StandardOAuth2Client,
    CLIENT_DTO extends StandardOAuth2ClientDTO,
    CLIENT_CREATE_DTO extends StandardOAuth2ClientCreateDTO,
    CLIENT_UPDATE_DTO extends StandardOAuth2ClientUpdateDTO>
    extends OAuth2ConfigureTemplate<SUBJECT, CERTIFICATE, CLIENT> {

    private static final int CLIENT_CREATE_DTO_GENERIC_INDEX = 4;
    private static final int CLIENT_UPDATE_DTO_GENERIC_INDEX = 5;

//    @ConditionalOnMissingBean
//    @Bean
//    public StandardOAuth2ClientBackendService<CLIENT_DTO, CLIENT_CREATE_DTO, CLIENT_UPDATE_DTO> standardOAuth2ClientBackendService(
//        StandardOAuth2ClientRepository<CLIENT> oauth2ClientClientRepository) throws NoSuchMethodException {
//        ResolvableType rt = ResolvableType.forClass(getClass()).as(OAuth2CoreConfigureTemplate.class);
//        Class<CLIENT> clientClass = (Class<CLIENT>) rt.getGeneric(CLIENT_GENERIC_INDEX).resolve();
//        Class<CLIENT_DTO> clientDTOClass = (Class<CLIENT_DTO>) rt.getGeneric(CLIENT_DTO_GENERIC_INDEX).resolve();
//        Objects.requireNonNull(clientClass, "clientClass can not be null");
//        Objects.requireNonNull(clientDTOClass, "clientDTOClass can not be null");
//
//        Constructor<CLIENT> clientConstructor = clientClass.getConstructor();
//        Constructor<CLIENT_DTO> clientDTOConstructor = clientDTOClass.getConstructor();
//        if (!Modifier.isPublic(clientConstructor.getModifiers())) {
//            throw new IllegalStateException(String.format("clientConstructor '%s' is not public", clientConstructor));
//        }
//        if (!Modifier.isPublic(clientDTOClass.getModifiers())) {
//            throw new IllegalStateException(String.format("clientDTOClass '%s' is not public", clientDTOClass));
//        }
//
//        return new StandardOAuth2ClientBackendServiceImpl<>(
//            oauth2ClientClientRepository, clientConstructor, clientDTOConstructor);
//    }

    @ConditionalOnBean(StandardOAuth2ClientBackendService.class)
    @Bean
    public StandardOAuth2ClientBackendController<CLIENT_DTO, CLIENT_CREATE_DTO, CLIENT_UPDATE_DTO> standardOAuth2ClientBackendController(
        StandardOAuth2ClientBackendService<CLIENT_DTO, CLIENT_CREATE_DTO, CLIENT_UPDATE_DTO> standardOAuth2ClientBackendService) {
        TypeResolver tr = new TypeResolver(getClass()).as(StandardOAuth2ConfigureTemplate.class);
        ParameterizedType pt = tr.asParameterizedType();
        Type clientCreateDTOType = pt.getActualTypeArguments()[CLIENT_CREATE_DTO_GENERIC_INDEX];
        Type clientUpdateDTOType = pt.getActualTypeArguments()[CLIENT_UPDATE_DTO_GENERIC_INDEX];
        Objects.requireNonNull(clientCreateDTOType, "clientCreateDTOType can not be null");
        Objects.requireNonNull(clientUpdateDTOType, "clientUpdateDTOType can not be null");
        return new StandardOAuth2ClientBackendController<>(
            standardOAuth2ClientBackendService, clientCreateDTOType, clientUpdateDTOType);
    }

}