## Usage

### 依赖接入

share层依赖：

```xml
<dependency>
    <groupId>fun.fengwk.convention4j</groupId>
    <artifactId>convention4j-oauth2-share</artifactId>
</dependency>
```
core层依赖：

```xml
<dependency>
    <groupId>fun.fengwk.convention4j</groupId>
    <artifactId>convention4j-oauth2-core</artifactId>
</dependency>
```
infra层依赖：

```xml
<dependency>
    <groupId>fun.fengwk.convention4j</groupId>
    <artifactId>convention4j-oauth2-infra</artifactId>
</dependency>
```

### 启用服务

- `OAuth2CoreConfigureTemplate`的实现类，实现类必须要指定所有泛型的具体类型。
- 默认情况下需要注入标准的客户端仓储实现`StandardOAuth2ClientRepository`，oauth2-infra提供了该仓储的抽象实现。
- 如果想要完全自定义客户端也可以实现自己的`OAuth2ClientManager`并在`OAuth2CoreConfigureTemplate`内部定义之前定义。
