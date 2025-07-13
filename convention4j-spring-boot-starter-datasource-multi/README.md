## Spring Boot 多数据源支持

通过介入 DataSource, TransactionManager, SqlSessionFactory, SqlSessionTemplate 的自动构建过程来支持多数据源。

### Usage

在原先`spring.datasource`路径上增加了`multi`属性用于存放多数据源配置:

```yaml
spring:
  datasource:
    multi:
      ds1:
        primary: true
        driver-class-name: org.h2.Driver
        url: jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MySQL
        username: SA
        properties:
          minimumIdle: 5
          maximumPoolSize: 30
          idleTimeout: 600000
          connectionTimeout: 30000
          leakDetectionThreshold: 300000
      ds2:
        driver-class-name: org.h2.Driver
        url: jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MySQL
        username: SA
```

如上配置启动了两个数据源`ds1`和`ds2`, 其中`primary`为`true`的为主数据源, 主数据源在使用方式上和以往的单数据源一致, 其它数据源在使用使用需要注意以下两点。

1. 在配置`@Transactional`注解时必须指定对应数据源的事务管理器, 命名规范为`{数据源名称}TransactionManager`, 下面是一个使用示例:

```java
@Transactional("ds2TransactionManager")
public Ds2DO insertAndQuery(String name) {
    Ds2DO ds2DO = new Ds2DO();
    ds2DO.populateDefaultFields();
    ds2DO.setName(name);
    ds2Mapper.insert(ds2DO);
    return ds2Mapper.getByName(name);
}
```

2. 在配置 Mybatis Mapper 扫描时需要指定`SqlSessionFactory`和`SqlSessionTemplate`, 命名规范为`{数据源名称}SqlSessionFactoryr`和`{数据源名称}SqlSessionTemplate`, 下面是一个使用示例:

```java
@MapperScan(markerInterface = BaseMapper.class,
    // 需要配置当前扫描目录下要使用的sqlSessionFactory和sqlSessionTemplate
    sqlSessionFactoryRef = "ds2SqlSessionFactory",
    sqlSessionTemplateRef = "ds2SqlSessionTemplate")
@Configuration
public class Ds2Configuration {
}
```