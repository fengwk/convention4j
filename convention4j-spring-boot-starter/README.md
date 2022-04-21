# Convention4j Spring Boot Starter

当前模块用于支持基础规约组件的自动装配。

# 自动检验

集成Spring校验器到`GlobalValidator`中，校验器详细使用方法见[auto-validation](https://github.com/fengwk/auto-validation)。

引入依赖：

```xml
<dependency>
    <groupId>fun.fengwk.auto-validation</groupId>
    <artifactId>auto-validation-validator</artifactId>
</dependency>
```

# 雪花ID

集成使用雪花算法并且保持命名空间隔离的ID生成器到Spring容器中。

引入依赖：

```xml
<dependency>
    <groupId>fun.fengwk.common4j</groupId>
    <artifactId>common4j-idgen</artifactId>
</dependency>
```

使用示例：

```java
@Component
public class Test {
    @Autowired
    private NamespaceIdGenerator<Long> idGenerator;
    // ...
    {
        long id = idGenerator.next(Test.class);
        // ...
    }
}
```

全局引用：

```java
long id = GlobalSnowflakeIdGenerator.next(Test.class);
```

## 固定workerId

使用雪花算法必须先指定当前节点的workerId，一种方式是使用配置形式指定固定的workerId。

配置`application.yml`：

```yaml
convention:
  snowflake-id:
    # 雪花算法起始时间
    initial-timestamp: 1630211105537
    # 当前节点编号[0, 1024)
    worker-id: 0
```

## 动态workerId

手动指定workerId比较繁琐，并且在一些场景下可能并不使用，因此规约另提供了一种动态获取workerId的方式，该方式需要依赖Redis服务。

引入Redis相关依赖：

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

配置`application.yml`：

```yaml
convention:
  snowflake-id:
    # 雪花算法起始时间
    initial-timestamp: 1630211105537
    
spring:
  # 默认情况下使用该redis配置的客户端实现动态workerId获取
  redis:
    password: a123
    sentinel:
      master: mymaster
      nodes:
        - redis.fengwk.fun:26379
        - redis.fengwk.fun:26380
        - redis.fengwk.fun:26381
```

# i18n

将`StringManager`加入Spring容器，提供国际化支持。

配置`application.yml`：

```yaml
convention:
  i18n:
    # 文件名前缀
    base-name: string
    # 当前当前语言环境
    locale: zh_CN
```

在resources下创建`string_zh_CN.properties`文件：

```properties
Test.message=你好
```

使用示例：

```java
@Component
public class Test {
    private StringManager stringManager;
    public Test(StringManagerFactory stringManagerFactory) {
        stringManager = stringManagerFactory.get(Test.class);
    }
    // ...
    {
        String message = stringManager.getString("message");
        // 你好
    }
}
```

全局引用：

```java
StringManager stringManager = GlobalStringManagerFactory.getStringManager(Test.class);
```

更多使用示例见[i18n](https://github.com/fengwk/commons/tree/main/commons-i18n)。

# 错误码

配置`application.yml`：

```yaml
convention:
  error-code:  
    i18n:
      # 指定当前语言环境，若不指定则有操作系统当前语言环境决定
      locale: zh_CN
```

使用示例：

```java
@Component
public class Test {
    private static final String TEST_ERROR_CODE = ErrorCode.encodeCode(ErrorCode.SOURCE_A, "TEST", "0001");
    @Autowired
    private ErrorCodeFactory errorCodeFactory;
    // ...
    {
        ErrorCode errorCode = errorCodeFactory.create(TEST_ERROR_CODE);
        throw errorCode.asThrowable();
    }
}
```

全局引用：

```java
ErrorCode errorCode = GlobalErrorCodeFactory.create(TEST_ERROR_CODE);
```

# 日志规约

在resource目录下创建`logback-spring.xml`，添加以下配置：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="fun/fengwk/convention/springboot/starter/logback/base.xml"/>
</configuration>
```

# EventBus

使用Guava的EventBus功能可以很好地实现JVM进程内的发布订阅功能。

引入依赖：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
</dependency>
```

使用示例：

```java
@Component
public class Test {
    
    @Autowired
    private EventBus eventBus;

}
```
