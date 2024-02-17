# Convention For Java Spring Boot Starter

Convention For Java Spring Boot Starter为SpringBoot项目提供支持，并且将规约组件与SpringBoot框架进行集成。

# 快速开始

## Auto Validation (@Deprecated)

集成Spring校验器到`GlobalValidator`中，使用时要先引入依赖：

```xml
<dependency>
	  <groupId>fun.fengwk.auto-validation</groupId>
	  <artifactId>auto-validation-validator</artifactId>
</dependency>
```

如果需要支持`@AutoValidation`则需额外引入依赖：

```xml
<dependency>
    <groupId>fun.fengwk.auto-validation</groupId>
    <artifactId>auto-validation-processor</artifactId>
    <version>0.0.13</version>
</dependency>
```

更多使用方法详细使用方法见[auto-validation](https://github.com/fengwk/auto-validation)。

## 雪花ID

集成使用雪花ID算法的`NamespaceIdGenerator<Long>`ID生成器到Spring容器中。

1、需要引入依赖：

```xml
<dependency>
    <groupId>fun.fengwk.common4j</groupId>
    <artifactId>common4j-idgen</artifactId>
</dependency>
```

2、使用示例：

```java
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

3、使用全局引用：

```java
long id = GlobalSnowflakeIdGenerator.next(Test.class);
```

### 固定workerId

使用雪花算法必须先指定当前节点的workerId，一种方式是使用配置形式指定固定的workerId，需要配置`application.yml`：

```yaml
convention:
  snowflake-id:
    # 雪花算法起始时间
    initial-timestamp: 1630211105537
    # 当前节点编号[0, 1024)
    worker-id: 0
```

### 动态workerId

手动指定workerId比较繁琐，并且在一些场景下可能并不使用，因此规约另提供了一种动态获取workerId的方式，该方式需要依赖Redis服务。

1、引入Redis相关依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

2、配置`application.yml`：

```yaml
convention:
  snowflake-id:
    # 雪花算法起始时间
    initial-timestamp: 1630211105537
    
spring:
  # 默认情况下使用该redis配置的客户端实现动态workerId获取
  data:
    redis:
      password: a123
      sentinel:
        master: mymaster
        nodes:
          - redis.fengwk.  fun:26379
          - redis.fengwk.fun:26380
          - redis.fengwk.fun:26381
```

## i18n

将StringManagerFactory继承到Spring容器，提供国际化支持。

1、配置`application.yml`：

```yaml
convention:
  i18n:
    # 文件名前缀，如不指定i18n自动配置将无法生效
    base-name: string
    # 指定当前语言环境，如不指定将使用系统默认的语言环境
    locale: zh_CN
```

2、在resources下创建`string_zh_CN.properties`文件：

```yaml
Test.message=你好
```

3、使用示例：

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

4、全局引用：

```java
StringManager stringManager = GlobalStringManagerFactory.getStringManager(Test.class);
```

## 错误码

1、配置`application.yml`：

```yaml
convention:
  error-code:  
    i18n:
      # 指定当前语言环境，如不指定将使用系统默认的语言环境
      locale: zh_CN
```

2、使用示例：

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

3、全局引用：

```java
ErrorCode errorCode = GlobalErrorCodeFactory.create(TEST_ERROR_CODE);
```

## 日志规约

在resource根目录下创建`logback-spring.xml`，添加以下配置：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="fun/fengwk/convention/springboot/starter/logback/base.xml"/>
</configuration>
```

## EventBus

使用Guava的EventBus功能可以很好地实现JVM进程内的发布订阅功能。

1、引入依赖：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
</dependency>
```

2、使用示例：

```java
@Component
public class Test {
    
    @Autowired
    private EventBus eventBus;
    
}
```