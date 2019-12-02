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
COPY target/chapter11-1-0.0.1-SNAPSHOT.jar app.jar
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