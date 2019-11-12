# springboot作为云原生开发服务要解决的问题

由于云服务与微服务浪潮下，springboot作为一个出色开发框架，它自身提供了哪些功能

## 应用监控

JMX
通过http health endpoint来提供。
基于springboot 的spring boot admin的快速搭建
SSH


## 基于springboot的微服务框架spring cloud

spring cloud利用springboot 特性提供了微服务框架的全家桶

1. 服务发现 eureka
2. 服务配置 spring config
3. 服务路由 ribbon
4. 服务熔断与限流 hystrix
5. 服务网关 zuul
6. 服务链路监控

## springboot应用docker镜像发布与运行
 
 dockerfile编写
 docker compose yaml文件编写
 docker运行

