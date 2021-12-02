# Convention4j Spring Boot Starter

当前模块用于支持基础规约组件的自动装配。

# 自动检验

集成Spring校验器到[auto-validation](https://github.com/fengwk/auto-validation)。

引入依赖：

```xml
<dependency>
    <groupId>fun.fengwk.auto-validation</groupId>
    <artifactId>auto-validation-validator</artifactId>
</dependency>
```

# 雪花ID

集成雪花ID生成器。

引入依赖：

```xml
<dependency>
    <groupId>fun.fengwk.common4j</groupId>
    <artifactId>common4j-idgen</artifactId>
</dependency>
```

配置`application.yml`：

```yaml
convention:
  snowflake-id:
    # 雪花算法起始时间
    initial-timestamp: 1630211105537
    # 当前节点编号[0, 1024)
    worker-id: 0
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

# i18n

国际化支持。

引入依赖：

```xml
<dependency>
    <groupId>fun.fengwk.common4j</groupId>
    <artifactId>common4j-i18n</artifactId>
</dependency>
```

配置`application.yml`：

```yaml
convention:
  i18n:
    # 文件名前缀
    base-name: string
    # 当前语言
    locale: zh_CN
```

在resources下创建`string_zh_CN.properties`文件：

```
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

更多使用示例见[i18n](https://github.com/fengwk/commons/tree/main/commons-i18n)

# 错误码

配置`application.yml`：

```yaml
convention:
  error-code:  
    i18n:
      base-name: string
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



