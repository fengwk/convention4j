Convention For Java Common模块为是规约的通用能力支撑模块，提供了两部分的能力：

1. 对编码规约的支持。
1. 通用工具的支持。

# 快速开始

下面将介绍common模块中常用的工具以及使用的方式，有所的示例代码都可以在convention4j-example模块中查找到。

## 编码规约

为了简化[编码规约](https://www.yuque.com/docs/share/ab08a303-e020-47ba-a2e3-ca426e3cabfb?# 《编码规约》)的落地，common模块提供了以下几个包去实现规约中的通用模型：

| **包** | **说明**                                                     |
| ------ | ------------------------------------------------------------ |
| code   | 状态码，可以实现[编码规约](https://www.yuque.com/docs/share/ab08a303-e020-47ba-a2e3-ca426e3cabfb?# 《编码规约》)中错误处理部分的异常码部分，提供了一些状态码相关的类来描述程序的运行状态，以及错误码异常来抛出相应的异常状态。 |
| page   | 分页，提供了普通分页、轻量级分页（无总数）、游标分页三种分页模型。 |
| result | 响应结果，对于服务调用结果的封装。                           |

## 类路径扫描

ClassPathScanner对类路径扫描提供了支持，可以通过ANT模式扫描出类路径下所有匹配的资源，以下示例将扫描出classpath中所有以.class为后缀的资源。

```java
public class ClassPathScannerExample {

    public static void main(String[] args) throws IOException {
        ClassPathScanner scanner = new ClassPathScanner(ClassPathScannerExample.class.getClassLoader());
        List<Resource> resources = scanner.scan("**/*.class");
        System.out.println(resources);
    }

}
```

## 系统时钟

Clock接口的目的是统一时钟接口，在下面的示例中将使用系统时钟输出毫秒与微秒。

```java
public class ClockExample {

    public static void main(String[] args) {
        Clock clock = new SystemClock();
        System.out.println("currentTimeMillis: " + clock.currentTimeMillis());
        System.out.println("currentTimeMicros: " + clock.currentTimeMicros());
    }

}
```

## 错误码

### 定义错误码

错误码定义规则见ErrorCode的Javadoc，或[编码规约](https://www.yuque.com/docs/share/ab08a303-e020-47ba-a2e3-ca426e3cabfb?# 《编码规约》)相应内容。实现错误码定义的推荐方式是实现一个继承CodeTable的枚举，如下示例创建了错误码`Example_0001`，可以使用ExampleCodeTable枚举来使用它。

```java
public enum ExampleCodeTable implements CodeTable {

    EXAMPLE_ERROR(encodeCode("0001"))

    ;

    private static final String EXAMPLE = "Example";

    private final String code;

    ExampleCodeTable(String code) {
        this.code = code;
    }

    static String encodeCode(String num) {
        return ErrorCode.encodeCode(EXAMPLE, num);
    }

    @Override
    public String getCode() {
        return code;
    }
}
```

### 创建错误码

使用ErrorCodeFactory的实现类可以创建相应错误码，尽管可以使用SimpleErrorCodeFactory常见错误码，但推荐使用I18nErrorCodeFactory支持国际化的错误码。

1、因为国际化能力依赖了ognl，因此首先要引入依赖：

```xml
<dependency>
    <groupId>ognl</groupId>
    <artifactId>ognl</artifactId>
</dependency>
```

2、为了支持国际化，应该在resource根路径下创建`error-code{_locale}.properties`文件描述错误码对应的描述内容，内容中可以使用`${}`定义引用内容，甚至可以在其中插入ognl表达式，创建错误码时的上下文内容最终将填充这些占位符。

```properties
Example_0001=hi ${name}, this is example error.
```

```properties
Example_0001=你好${name}，这是一个示例错误。
```

3、接下来可以使用相应语言的工厂创建对应语言的错误码。

```java
public class ErrorCodeExample {
    
    public static void main(String[] args) {
        // 英语
        I18nErrorCodeFactory enErrorCodeFactory = new I18nErrorCodeFactory(Locale.ENGLISH,
                                                                           ErrorCodeExample.class.getClassLoader());
        ErrorCode enErrorCode = enErrorCodeFactory.create(ExampleCodeTable.EXAMPLE_ERROR,
                                                          MapUtils.newMap("name", "fengwk"));
        System.out.println(enErrorCode);
        // 输出：<Example_0001, hi fengwk, this is example error.>
        
        // 中文
        I18nErrorCodeFactory cnErrorCodeFactory = new I18nErrorCodeFactory(Locale.CHINA,
                                                                           ErrorCodeExample.class.getClassLoader());
        ErrorCode cnErrorCode = cnErrorCodeFactory.create(ExampleCodeTable.EXAMPLE_ERROR,
                                                          MapUtils.newMap("name", "fengwk"));
        System.out.println(cnErrorCode);
        // 输出：<Example_0001, 你好fengwk，这是一个示例错误。>
    }
    
}
```

### 抛出错误码异常

常见的错误码处理方式是抛出相应的错误码异常，需要注意的是规约推荐在抛出错误码异常前首先记录可能会使用到的日志，因为此时的上下文信息是最完整的利于日志信息的记录。

```java
public class ThrowableErrorCodeExample {

    private static final Logger log = LoggerFactory.getLogger(ThrowableErrorCodeExample.class);

    public static void main(String[] args) {
        I18nErrorCodeFactory errorCodeFactory = new I18nErrorCodeFactory(Locale.getDefault(),
                ErrorCodeExample.class.getClassLoader());
        ErrorCode errorCode = errorCodeFactory.create(ExampleCodeTable.EXAMPLE_ERROR,
                MapUtils.newMap("name", "fengwk"));

        log.warn("发生了示例错误, 上下文信息是balabala...");
        throw errorCode.asThrowable();
    }

}
```

## 命名线程工厂

当我们使用线程池自动创建线程时默认创建的线程是没有一个易于理解和区分的名称的，使用NamedThreadFactory可以解决这一问题。

```java
public class NamedThreadFactoryExample {

    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors() * 10,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1024),
                new NamedThreadFactory("示例线程"),
                new ThreadPoolExecutor.AbortPolicy());

        for (int i = 0; i < 100; i++) {
            executor.execute(() -> {
                System.out.println(Thread.currentThread().getName());
            });
        }
    }

}
```

## Gson

gson包中定义了一套规约约定的序列化和反序列化规则，所有的扩展规则都是GsonBuilderConfigurator的实现类，使用SPI机制进行加载。

1、如果要使用Gson必须先引入依赖：

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
</dependency>
```

2、推荐使用GlobalGson访问单例对象以减少创建开销：

```java
public class GlobalGsonExample {
    
    public static void main(String[] args) {
        Gson gson = GlobalGson.getInstance();
        String json = gson.toJson(MapUtils.newMap("name", "fengwk"));
        System.out.println(json);
    }
    
}
```

## i18n

1、如果需要使用国际化支持必须引入依赖：

```xml
<dependency>
    <groupId>ognl</groupId>
    <artifactId>ognl</artifactId>
</dependency>
```

2、在resource根目录下添加相应的本地化语言文件：

```properties
fun.fengwk.convention4j.example.i18n.Strings.greeting=hi, ${name}.
```

```properties
fun.fengwk.convention4j.example.i18n.Strings.greeting=你好，${name}。
```

### 编程模式

最简单的方式是使用StringManagerFactory构建StringManager获取指定语言的字符串：

```java
public class StringManagerExample {

    public static void main(String[] args) {
        ClassLoader cl = StringManagerExample.class.getClassLoader();

        // 英语
        StringManagerFactory enStringManagerFactory = new StringManagerFactory(
                ResourceBundle.getBundle("message", Locale.ENGLISH,
                        cl, AggregateResourceBundle.CONTROL));
        StringManager enStringManager = enStringManagerFactory.getStringManager("fun.fengwk.convention4j.example.i18n.Strings.");
        System.out.println(enStringManager.getString("greeting", MapUtils.newMap("name", "fengwk")));
        // 输出：hi, fengwk.

        // 中文
        StringManagerFactory cnStringManagerFactory = new StringManagerFactory(
                ResourceBundle.getBundle("message", Locale.CHINA,
                        cl, AggregateResourceBundle.CONTROL));
        StringManager cnStringManager = cnStringManagerFactory.getStringManager("fun.fengwk.convention4j.example.i18n.Strings.");
        System.out.println(cnStringManager.getString("greeting", MapUtils.newMap("name", "fengwk")));
        // 输出：你好，fengwk。
    }

}
```

### 代理模式

使用代理模式可以使指定语言字符串获取方式更加优雅：

```java
public class ProxyExample {

    public static void main(String[] args) {
        ClassLoader cl = StringManagerExample.class.getClassLoader();

        // 英语
        StringManagerFactory enStringManagerFactory = new StringManagerFactory(
                ResourceBundle.getBundle("message", Locale.ENGLISH,
                        cl, AggregateResourceBundle.CONTROL));
        Strings enStrings = enStringManagerFactory.getStringManagerProxy(Strings.class, cl);
        System.out.println(enStrings.greeting("fengwk"));
        // 输出：hi, fengwk.

        // 中文
        StringManagerFactory cnStringManagerFactory = new StringManagerFactory(
                ResourceBundle.getBundle("message", Locale.CHINA,
                        cl, AggregateResourceBundle.CONTROL));
        Strings cnStrings = cnStringManagerFactory.getStringManagerProxy(Strings.class, cl);
        System.out.println(cnStrings.greeting("fengwk"));
        // 输出：你好，fengwk。
    }

}
```

## ID生成器

提供了UUIDGenerator和SnowflakesIdGenerator，如果使用雪花ID生成器，那么必须要选择一个workerId客户端获取当前雪花ID生成器的workerId，如果使用RedisWorkerIdClient那么又必须选择合适的Redis脚本执行器。

使用RedisTemplateScriptExecutor需要引入依赖：

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-redis</artifactId>
</dependency>
```

使用JedisPoolScriptExecutor需要引入依赖：

```xml
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
</dependency>
```

## 迭代器

iterator包是对迭代器的增强库，所有入口都集中于Iterators类，详细使用方法可见其Javadoc。

## JWT

如果要使用JWT，需要引入依赖：

```xml
<dependency>
    <groupId>fun.fengwk.jwt4j</groupId>
    <artifactId>jwt4j</artifactId>
    <optional>true</optional>
</dependency>
```

使用方法详见[jwt4j](https://github.com/fengwk/jwt4j)。

## 日志

统一的日志格式能够更方便查阅或进行脚本处理，Log类是对Logger规范化包装，将日志的概念划分为：

- 记录（必须）：日志记录的具体内容，主要供人来查阅。
- 上下文（可选）：供人查阅定位日志产生的原因，供脚本进行日志存档或数据修复等工作。

使用Log需要引入依赖：

```xml
<dependency>
    <groupId>ognl</groupId>
    <artifactId>ognl</artifactId>
</dependency>
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
</dependency>
<!-- 以及任一slf4j-api实现 -->
```

## 分页

### 普通分页

提供了普通分页模型，适用于需要跳页，统计总数的场景。

- PageQuery
- Page

```java
public class PageExample {
    
    public static void main(String[] args) {
        PageQuery pageQuery = new PageQuery(1, 10);
        Page<Object> page = Pages.page(pageQuery, Collections.emptyList(), 0);
    }
    
}
```

### 轻分页

轻分页是为了提升分页性能而设计的，它不支持总数统计，仅支持检查页面是否还能下翻（通过每次多查询一个元素实现）。

- LitePageQuery
- LitePage

```java
public class LitePageExample {

    public static void main(String[] args) {
        LitePageQuery litePageQuery = new LitePageQuery(1, 10);
        LitePage<Object> litePage = Pages.litePage(litePageQuery, Collections.emptyList());
    }

}
```

### 游标分页

游标分页是在轻分页之上的性能再提升，也仅支持检查页面是否还能下翻，但限制更加严格，必须要有一个有序的游标键来确保每次查询能够基于上次结果的游标键缩小查询的结果集。

- CursorPageQuery
- CursorPage

## 类型解析

TypeResolver可以帮助用户从复杂的类型关系中找到感兴趣的类型，比如下面示例中的泛型查找：

```java
public class TypeResolverExample {

    static class Parent<T> {}

    static class Children extends Parent<String> {}

    public static void main(String[] args) {
        TypeResolver typeResolver = new TypeResolver(Children.class);
        System.out.println(typeResolver.as(Parent.class).asParameterizedType().getActualTypeArguments()[0]);
    }

}
```

## Result

任何方法的调用结果都可能产生异常，如果是本地调用可能会通过Java异常语法向上抛出，但对于RPC调用这并不容易实现，除非所有工程中都具有统一的异常库，另一种可行的方式是使用Result返回调用结果，并使用错误码标识相应的异常信息。

## MySql5xError

对MySQL 5.x版本异常码的封装。

## AntPattern

ANT模式匹配器。

## Property

如果使用一个Bean中的属性，可以采取字符串硬编码的方式，不过一旦修改属性名称，这种方式就很难被修改，使用Property就可以使用方法引用形式获取Bean属性，这种方式的好处是一旦要修改属性名称，IDEA可以帮助我们进行全局修改。

## Ref

Java中并没有指针的概念，使用Ref替代指针是不错的选择。