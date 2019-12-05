# springboot作为云原生开发服务要解决的问题

由于云服务与微服务浪潮下，springboot作为一个出色开发框架，它自身提供了哪些功能

## spring boot 与devops(CI/CD)

* jenkins 自动化部署 spring boot 项目

Jenkins 的主要目标是监控软件开发流程，快速显示问题，保证开发人员以及相关人员省时省力提高开发效率。CI 系统在整个开发过程中的主要作用是控制：当系统在代码存储库中探测到修改时，它将运行构建的任务委托给构建过程本身。如果构建失败了，那么 CI 系统将通知相关人员，然后继续监视存储库。它的角色看起来是被动的；但它确能快速反映问题。

Jenkins 特点：

1. Jenkins一切配置都可以在 web 界面上完成。有些配置如 MAVEN_HOME 和 Email，只需要配置一次，所有的项目就都能用。当然也可以通过修改 XML 进行配置。
2. 支持 Maven 的模块 (Module)，Jenkins 对 Maven 做了优化，因此它能自动识别 Module，每个 Module 可以配置成一个 job。相当灵活。
3. 测试报告聚合，所有模块的测试报告都被聚合在一起，结果一目了然，使用其他 CI，这几乎是件不可能完成的任务。
4. 构件指纹(artifact fingerprint)，每次 build 的结果构件都被很好的自动管理，无需任何配置就可以方便的浏览下载。

## 下载运行配置jenkis

通过安装war或者 brew install jenkins
![JenkinsInstallation.png](./springboot与云原生态/imgs/JenkinsInstallation.png)
重新设置Admin信息
![JenkinsResetAdmin.png](./springboot与云原生态/imgs/JenkinsResetAdmin.png)
安装需要的插件
![JenkinsPluginInstallation.png](./springboot与云原生态/imgs/JenkinsPluginInstallation.png)
Jenkins首页
![JenkinsIndex.png](./springboot与云原生态/imgs/JenkinsIndex.png)
Jenkins配置project
![JenkinsWorkItemConfiguration.png](./springboot与云原生态/imgs/JenkinsWorkItemConfiguration.png)
然后可以build project
![JenkinsBuild.png](./springboot与云原生态/imgs/JenkinsBuild.png)
查看build的日志和结果
![JenkinsBuildResult.png](./springboot与云原生态/imgs/JenkinsBuildResult.png)
可以通过post build来配置CD
![postAsContinualDeploy.png](./springboot与云原生态/imgs/postAsContinualDeploy.png)

## springboot应用docker镜像发布与运行

### docker maven 插件

1. docker-maven-plugin

插件允许没有DockerFile，相关配置参数全部写在pom.xml中。

```text
<plugin>
<groupId>com.spotify</groupId>
<artifactId>docker-maven-plugin</artifactId>
<version>1.2.1</version>
<configuration>
    <imageName>${docker.image.prefix}/${project.artifactId}</imageName>
    <dockerDirectory>src/main/docker</dockerDirectory>
    <resources>
        <resource>
            <targetPath>/</targetPath>
            <directory>${project.build.directory}</directory>
            <include>${project.build.finalName}.jar</include>
        </resource>
    </resources>
</configuration>
</plugin>
```

2. dockerfile-maven-plugin
连接远程Docker，只要一个命令就能把本地的jar包打成Docker镜像，命令执行完毕后，服务器上就会有刚打包好的镜像，此时再执行该镜像即可。

```text
<plugin>
<groupId>com.spotify</groupId>
<artifactId>dockerfile-maven-plugin</artifactId>
<version>1.4.13</version>
<executions>
    <execution>
        <id>default</id>
        <!-- 指定绑定的阶段，是在install 阶段，所以在package之前的都是不执行的-->
        <phase>install</phase>
        <goals>
         <!-- 执行的docker的命令 -->
            <goal>build</goal>
            <!--<goal>push</goal>-->
        </goals>
    </execution>
</executions>
<configuration>
    <!-- 指定repository名字，和docker build -t 命令后面的参数一致-->
    <repository>hu-study/chapter11-1</repository>
    <tag>${project.version}</tag>
    <buildArgs>
        <JAR_FILE>${project.build.finalName}.jar</JAR_FILE>
    </buildArgs>
</configuration>
</plugin>
```

 dockerfile编写

```dockfile
# 指定该镜像的基础镜像
FROM openjdk:8-jdk-alpine
# 指定作者信息
MAINTAINER huqianghui <huqianghui0613@163.com>
# 容器运行时应该尽量保持容器存储层不发生写操作,应该保存于卷(volume)中
# 防止运行时用户忘记将动态文件所保存目录挂载为卷，在 Dockerfile 中，可以事先指定某些目录挂载为匿名卷，
# 这样在运行时如果用户不指定挂载，其应用也可以正常运行，不会向容器存储层写入大量数据
# 这里的 /tmp 目录就会在运行时自动挂载为匿名卷，任何向 /tmp 中写入的信息都不会记录进容器存储层，从而保证了容器存储层的无状态化
# 运行时可以覆盖这个挂载设置 docker run -d -v mydata:/tmp xxxx
# 使用了 mydata 这个命名卷挂载到了 /tmp 这个位置，替代了 Dockerfile 中定义的匿名卷的挂载配置
# tomcat启动之后，一些临时碎片文件会写入temp目录，所以这里挂载一个目录
VOLUME /temp
# ADD 指令和 COPY 的格式和性质基本一致。但是在 COPY 基础上增加了一些功能。
# 比如 <源路径> 可以是一个 URL，这种情况下，Docker 引擎会试图去下载这个链接的文件放到 <目标路径> 去。
# 自动解压文件。如果 <源路径> 为一个 tar 压缩文件的话，压缩格式为 gzip, bzip2 以及 xz 的情况下，ADD 指令将会自动解压缩这个压缩文件到 <目标路径> 去
# ADD 指令会令镜像构建缓存失效，从而可能会令镜像构建变得比较缓慢。
# 因此在 COPY 和 ADD 指令中选择的时候，可以遵循这样的原则，所有的文件复制均使用 COPY 指令，仅在需要自动解压缩的场合使用 ADD。
COPY target/chapter1-1-0.0.1-SNAPSHOT.jar app.jar
# EXPOSE 指令是声明运行时容器提供服务端口，
# 这只是一个声明，在运行时并不会因为这个声明应用就会开启这个端口的服务。
# 在 Dockerfile 中写入这样的声明有两个好处，一个是帮助镜像使用者理解这个镜像服务的守护端口，以方便配置映射；
# 另一个用处则是在运行时使用随机端口映射时，也就是 docker run -P 时，会自动随机映射 EXPOSE 的端口。
# 要将 EXPOSE 和在运行时使用 -p <宿主端口>:<容器端口> 区分开来。
# -p，是映射宿主端口和容器端口，换句话说，就是将容器的对应端口服务公开给外界访问
# 而 EXPOSE 仅仅是声明容器打算使用什么端口而已，并不会自动在宿主进行端口映射。
EXPOSE 8090
# ENTRYPOINT 的格式和 RUN 指令格式一样，分为 exec 格式和 shell 格式。但是run在构建镜像时执行脚本。
# ENTRYPOINT 的目的和 CMD 一样，都是在指定容器启动程序及参数
# ENTRYPOINT 在运行时也可以替代，不过比 CMD 要略显繁琐，需要通过 docker run 的参数 --entrypoint 来指定。
# 当指定了 ENTRYPOINT 后，CMD 的含义就发生了改变，不再是直接的运行其命令，而是将 CMD 的内容作为参数传给 ENTRYPOINT 指令
# 换句话说实际执行时，将变为：
   #<ENTRYPOINT> "<CMD>"
ENTRYPOINT ["java","-jar","/app.jar"]
```

### docker运行

 ```text
 docker run -p 8090:8090 -d  hu-study/chapter11-1:0.0.1-SNAPSHOT
 ```

***参照代码chapter11-1***

## 基于springboot的微服务框架spring cloud

spring cloud利用springboot 特性提供了微服务框架的全家桶

1. 服务发现 eureka
2. 服务配置 spring config
3. 服务路由 ribbon
4. 服务熔断与限流 hystrix
5. 服务网关 zuul
6. 服务链路监控

我们通过构建一个最简单的eureka，springboot-admin，hello-server来组成一个练习用的微服务。

***参照代码chapter12-1***