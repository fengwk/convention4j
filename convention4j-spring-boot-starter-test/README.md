# Convention For Java Spring Boot Starter Test

Convention For Java Spring Boot Starter Test模块为SpringBoot项目提供测试支持。

1、使用`@EnableTestRedisServer`注解可以启用内嵌的Redis服务器。<br />2、使用`@EnableTestSnowflakeId`注解可以自动启用雪花ID生成器。<br />3、支持h2内嵌数据库，如需使用内嵌数据库需要采用如下`application.yml`配置：

```yaml
spring:
  sql:
    init:
      # SqlInitializationProperties
      schema-locations:
        - classpath:schema-h2.sql
      data-locations:
        - classpath:data-h2.sql

  datasource:
    url: jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MySQL
    username: SA
#    url: jdbc:mysql://mysql.fengwk.fun:3306/simple_user_system?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&connectTimeout=3000&socketTimeout=3000&useSSL=false
#    driver-class-name: com.mysql.jdbc.Driver
#    username: fengwk
#    password: a123
#    # 默认只会初始化内嵌的h2数据库，使用always可以使mysql数据库也得到初始化
#    initialization-mode: always
```