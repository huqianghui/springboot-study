# 快速开发运行一个springboot应用

通过简单的步骤搭建一个web应用程序，完成helloworld

## 本地开发环境准备

1. java8的本地暗转
2. IDE的下载与安装 eclipse，sbt，intellij
3. maven的下载与配置
  
## spring的发展历史与社区

***思考问题1：与EJB相比，spring有哪些优势。为何它发展起来了。对做技术选型有哪些参考价值***

spring1.x =》 2.x => 3.x =>4.x => 5.x特性变迁

1. spring 1.x POJO的xml配置来取代EJB的复杂继承关系
2. spring 2.x 使用java5的新特性--注解，实现部分AOP功能
3. spring 3.x 启用约定优先于配置的方式，支持使用代码配置
4. spring 4.x 泛型的更好支持，脚本JVM语言groovy的引入，支持更好的部署配置
5. spring 5.x 全面Java8的增强，已经SpringWebFlux模块，响应编程的引入

## 通过样板工程快速启动一个spring boot 工程

演示内容如下：

1. 一个空的spring boot 应用工程
2. 代码配置与xml文件配置分别演示
   注解@Configuration，@Bean，@ComponentSCan的分别作用
   @Configuration 和 @AutoConfiguration 的区别
   Spring.factories的介绍，以及模块自制的介绍
3. 外部资源property文件配置的使用（JDBC）
4. JDBCTemplate的配置，以及使用默认JPA的区别和演示说明AutoConfiguration的便利
5. SpringMVC引入增加Controller 和 RestController的helloworld
6. 两种运行方式，jar 和 war，内嵌tomcat
7. 学会debug程序
8. 开心一刻 banner的客户化
9. 入口SpringAppliction 和applicationContext 关系说明。web.xml的去除
10. maven的pom文件说明
11. application配置文件property和yaml