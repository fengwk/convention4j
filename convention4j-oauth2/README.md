## Usage

### Server

1. 添加依赖

```xml
<dependency>
    <groupId>fun.fengwk.convention4j</groupId>
    <artifactId>convention4j-oauth2-server</artifactId>
</dependency>
<dependency>
  <groupId>fun.fengwk.convention4j</groupId>
  <artifactId>convention4j-oauth2-infra</artifactId>
</dependency>
```

2. 实现下面2个接口可以使`OAuth2Service`能够正常工作:
    - `OAuth2ClientManager`
    - `OAuth2SubjectManager`

3. 如果需要对外提供OAuth2服务，可以继承`OAuth2ControllerTemplate`，该模板类已经提供了默认OAuth2接口实现，只需要添加所需的MVC相关注解即可，下面是一个简单的例子：

```java
@RestController
public class OAuth2Controller extends OAuth2ControllerTemplate<UserSubjectDTO, UserCertificateDTO> {

    public OAuth2Controller(OAuth2Properties oauth2Properties,
                            OAuth2Service<UserSubjectDTO, UserCertificateDTO> oauth2Service) {
        super(oauth2Properties, oauth2Service);
    }

    /**
     * 单点登陆authorize
     *
     * @return 授权uri
     */
    @PostMapping(value = "/api/oauth2/sso")
    @Override
    public Result<String> sso(
            @RequestParam("responseType") String responseType,
            @RequestParam("clientId") String clientId,
            @RequestParam("redirectUri") String redirectUri,
            @RequestParam(value = "scope", required = false) String scope,
            @RequestParam(value = "state", required = false) String state,
            HttpServletRequest request) {
        return super.sso(
                responseType,
                clientId,
                redirectUri,
                scope,
                state,
                request);
    }

    /**
     * 授权码模式、隐式模式
     *
     * @return 授权uri
     */
    @PostMapping(value = "/api/oauth2/authorize")
    @Override
    public Result<String> authorize(
            @RequestParam("responseType") String responseType,
            @RequestParam("clientId") String clientId,
            @RequestParam("redirectUri") String redirectUri,
            @RequestParam(value = "scope", required = false) String scope,
            @RequestParam(value = "state", required = false) String state,
            @RequestBody(required = false) UserCertificateDTO certificate,
            HttpServletRequest request,
            HttpServletResponse response) {
        return super.authorize(
                responseType,
                clientId,
                redirectUri,
                scope,
                state,
                certificate,
                request,
                response);
    }

    /**
     * 授权码模式、客户端模式、密码模式、刷新令牌（clientId、clientSecret、refreshToken）
     *
     * @return 令牌
     */
    @PostMapping("/api/oauth2/token")
    @Override
    public Result<OAuth2TokenDTO> token(
            @RequestParam("grantType") String grantType,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam("redirectUri") String redirectUri,
            @RequestParam("clientId") String clientId,
            @RequestParam("clientSecret") String clientSecret,
            @RequestParam(value = "scope", required = false) String scope,
            @RequestParam(value = "subjectId", required = false) String subjectId,
            @RequestParam(value = "refreshToken", required = false) String refreshToken,
            @RequestBody(required = false) UserCertificateDTO certificate,
            HttpServletRequest request,
            HttpServletResponse response) {
        return super.token(
                grantType,
                code,
                redirectUri,
                clientId,
                clientSecret,
                scope,
                subjectId,
                refreshToken,
                certificate,
                request,
                response);
    }

    /**
     * 获取授权访问主体信息，有两种令牌设置途径，直接传递参数或在header的Authorization中传递Bearer token
     *
     * @param authorization 授权信息，{@link TokenType#buildAuthorization(String)}
     * @param scope       主动指定作用域
     * @return 主体信息
     */
    @GetMapping("/api/oauth2/user")
    public Result<UserSubjectDTO> user(
            @RequestHeader(value = TokenType.AUTHORIZATION) String authorization,
            @RequestParam(value = "scope", required = false) String scope) {
        return super.subject(authorization, scope);
    }

    /**
     * 回收令牌，有两种令牌设置途径，直接传递参数或在header的Authorization中传递Bearer token
     *
     * @param authorization 授权信息，{@link TokenType#buildAuthorization(String)}
     */
    @DeleteMapping("/api/oauth2/token")
    @Override
    public Result<Void> revokeToken(@RequestHeader(value = TokenType.AUTHORIZATION) String authorization) {
        return super.revokeToken(authorization);
    }

}
```

## 持久化Token

默认情况下使用Redis存储Token（Redis存储允许自动失效令牌，带来的副作用是如果调大客户端过期时间对已生成的令牌不会生效，当然这不是一定的因为令牌刷新时会更新TTL，因此设置较小的刷新时间将会缓解这一情况），如果希望使用持久化存储，一种方式是实现自定义的`OAuth2TokenRepository`，如果正在使用Mysql可以使用``开启，同时需要使用下面的SQL初始化令牌表：

```sql
create table if not exists oauth2_token (
    id                   bigint unsigned not null comment '用户系统id',
    client_id            varchar(64) not null comment '客户端id',
    subject_id           varchar(64) not null comment '主体id',
    scope                varchar(128) not null comment '作用域',
    token_type           varchar(64) not null comment '令牌类型',
    access_token         varchar(128) not null comment '访问令牌',
    refresh_token        varchar(128) not null comment '刷新令牌',
    sso_id               varchar(128) not null comment '单点登陆id',
    last_refresh_time    datetime(3) not null comment '最后一次刷新的时间',
    authorize_time       datetime(3) not null comment '授权的时间',
    create_time          datetime(3) not null default current_timestamp(3) comment '创建时间',
    update_time          datetime(3) not null default current_timestamp(3) on update current_timestamp(3) comment '更新时间',
    version              bigint not null default '0' comment '数据版本号',
    primary key (id),
    unique uk_accessToken (access_token),
    unique uk_refreshToken (refresh_token),
    unique uk_ssoId (sso_id),
    index idx_clientId_subjectId (client_id, subject_id),
    index idx_subjectId (subject_id),
    index idx_authorizeTime (authorize_time)
) engine=InnoDB default charset=utf8mb4 comment='oauth2令牌表';
```