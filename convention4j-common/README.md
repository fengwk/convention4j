# Convention4j Common

Common模块是通用库。为了减少不必要的依赖，在使用某些特定特功能时需要进行额外的依赖引入。



使用i18n的表达式功能需要依赖：

```xml
<dependency>
    <groupId>ognl</groupId>
    <artifactId>ognl</artifactId>
</dependency>
```



使用RedisTemplateScriptExecutor需要依赖：

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
</dependency>
```



使用JedisPoolScriptExecutor需要依赖：

```xml
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
</dependency>
```