# Convention For Java

Convention For Java的目标是为Java开发提供标准层工具，详细开发规约可见[编码规约](https://www.yuque.com/docs/share/ab08a303-e020-47ba-a2e3-ca426e3cabfb?# 《编码规约》)。

Convention For Java共分为四个模块，模块的层次关系如下图所示：<br />![](https://cdn.nlark.com/yuque/0/2022/jpeg/12973773/1651573480771-2e424bcf-3271-4e52-ba3f-49504ae6ed0f.jpeg)

其中每个模块都是为了实现不同的目标：

| **模块**                                                     | **说明**                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| [convention4j-common](https://www.yuque.com/docs/share/6e56c058-8827-4f6a-88c7-33635382dead?# 《convention4j-common》) | 提供通用能力支持。                                           |
| [convention4j-spring-boot-starter-test](https://www.yuque.com/docs/share/1bb16617-6c2d-45b2-a2c2-a14299df4c96?# 《convention4j-spring-boot-starter-test》) | 为SpringBoot项目提供测试支持。                               |
| [convention4j-spring-boot-starter](https://www.yuque.com/docs/share/36ea3531-cfe8-4f5a-bf7b-5d9164093526?# 《convention4j-spring-boot-starter》) | 为SpringBoot项目提供支持，并且将规约组件与SpringBoot框架进行集成。 |
| [convention4j-spring-boot-starter-web](https://www.yuque.com/docs/share/30cbe514-db7d-457b-afb8-7f21b9d6505f?# 《convention4j-spring-boot-starter-web》) | 为SpringBoot web项目提供支持：<br />1. 支持一些规约组件与框架的集成。<br />1. 约定web项目中规范，如通用错误处理、JSON消息转换等。<br /> |

## Quick Start

```shell
mvn archetype:generate -DarchetypeGroupId=fun.fengwk.convention4j -DarchetypeArtifactId=convention4j-archetype -DarchetypeVersion=0.0.33
```
