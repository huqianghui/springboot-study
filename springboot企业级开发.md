# springboot在企业级开发供对模块功能的支持

## 安全控制spring security的结合

![springSecurityURP](./springboot企业级开发/imgs/springSecurityURP.png)
如上图，是一种通用的用户权限模型（URP）。一般情况下会有5张表，分别是：用户表，角色表，权限表，用户角色关系表，角色权限对应表。
一般，资源分配时是基于角色的（即，资源访问权限赋给角色，用户通过角色进而拥有权限）；
而访问资源的时候是基于资源权限去进行授权判断的。

Spring Security依赖Spring，其功能强大。Spring Security致力于为Java应用提供认证和授权管理。它是一个强大的，高度自定义的认证和访问控制框架。
这句话包括两个关键词：Authentication（认证）和 Authorization（授权，也叫访问控制）

认证是验证用户身份的合法性，而授权是控制你可以做什么。

简单地来说，认证就是你是谁，授权就是你可以做什么。

常规配置如下：

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.formLogin() // 定义当需要用户登录时候，转到的登录页面。
        .loginPage("/login")// 设置登录页面
        .loginProcessingUrl("/user/login")// 自定义的登录接口
        .defaultSuccessUrl("/home").permitAll()// 登录成功之后，默认跳转的页面
        .and().authorizeRequests()// 定义哪些URL需要被保护、哪些不需要被保护
        .antMatchers("/", "/index"，"/user/login").permitAll()// 设置所有人都可以访问登录页面
        .anyRequest().authenticated()// 任何请求,登录后可以访问
        .and().csrf().disable();// 关闭csrf防护
}
```

### 自定义用户米密码的安全控制

自定义默认用户名和密码
在配置文件配置用户名和密码：

```text
spring.security.user.name=admin
spring.security.user.password=admin
spring.security.user.roles=admin
```

* springSecurity 实现方式是通过过滤器实现，有如下过滤器
![filter](./springboot企业级开发/imgs/springSecurityFilters.png)

* permitAll实现逻辑，通过维护Map来放行(PermitAllSupport.java)
![permitAll](./springboot企业级开发/imgs/permitAllUrlMap.png)

* 不需要认证的页面显示
![freedom](./springboot企业级开发/imgs/freedomPage.png)

* 关闭默认的安全访问控制(UsernamePasswordAuthenticationFilter.java)

旧版的 spring security 关闭默认安全访问控制，只需要在配置文件中关闭即可：

```text
security.basic.enabled = false
```

新版本 Spring-Boot2.xx(Spring-security5.x) 的不再提供上述配置了：

1. 方法1： 将 security 包从项目依赖中去除。
2. 方法2：将org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration不注入spring中：

```java
@SpringBootApplication
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
public class SecurityDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityDemoApplication.class, args);
    }

}
```

3. 实现一个配置类继承自WebSecurityConfigurerAdapter，并重写configure(HttpSecurity http)方法

```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/**").permitAll();
    }

    /**
    * 配置一个userDetailsService Bean
    * 不再生成默认security.user用户
    */
    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        return super.userDetailsService();
    }
}
```

***WebSecurityConfigurerAdapter是一个适配器类，所以为了使自定义的配置类见名知义，所以写成了WebSecurityConfig。同时增加了@EnableWebSecurity注解到了 spring security 中。***

***配置用户名与密码方式 参照chapter6-1***

### 结合数据库或者JWT动态校验的安全控制

在开始集成之前，我们先简单了解几个接口：

* AuthenticationProvider
AuthenticationProvider接口是用于认证的，可以通过实现这个接口来定制我们自己的认证逻辑，它的实现类有很多，默认的是JaasAuthenticationProvider

它的全称是 Java Authentication and Authorization Service (JAAS)
![AuthenticationProvider](./springboot企业级开发/imgs/AuthenticationProvider.png)

* AccessDecisionManager
AccessDecisionManager是用于访问控制的，它决定用户是否可以访问某个资源，实现这个接口可以定制我们自己的授权逻辑。
![AccessDecisionManager](./springboot企业级开发/imgs/AccessDecisionManager.png)

* AccessDecisionVoter
AccessDecisionVoter是投票器，在授权的时通过投票的方式来决定用户是否可以访问，这里涉及到投票规则。
![AccessDecisionVoter](./springboot企业级开发/imgs/AccessDecisionVoter.png)

* UserDetailsService
UserDetailsService是用于加载特定用户信息的，它只有一个接口通过指定的用户名去查询用户。
![UserDetailsService](./springboot企业级开发/imgs/UserDetailsService.png)

* UserDetails
UserDetails代表用户信息，即主体，User是它的一个实现。

自定义认证逻辑，实现org.springframework.security.core.userdetails.UserDetailsService接口，重载loadUserByUsername(String username)方法，当用户登录时，会调用UserDetailsService接口的loadUserByUsername()来校验用户的合法性。

```java
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ADMIN"));
        return new User("root", new BCryptPasswordEncoder().encode("root"), authorities);
    }
}
```

对于返回的UserDetails实现类，可以使用框架自己的 User，也可以自己实现一个 UserDetails 实现类，其中密码和权限都应该从数据库中读取出来，而不是写死在代码里。

### 实现单点登录的安全控制

使用 OAuth2 是向认证服务器申请令牌，客户端拿这令牌访问资源服务服务器，资源服务器校验了令牌无误后，如果资源的访问用到用户的相关信息，那么资源服务器还需要根据令牌关联查询用户的信息。

使用 JWT 是客户端通过用户名、密码 请求服务器获取 JWT，服务器判断用户名和密码无误之后，可以将用户信息和权限信息经过加密成 JWT 的形式返回给客户端。在之后的请求中，客户端携带 JWT 请求需要访问的资源，如果资源的访问用到用户的相关信息，那么就直接从JWT中获取到。

所以，如果我们在使用 OAuth2 时结合JWT ，就能节省集中式令牌校验开销，实现无状态授权认证。

需要两个工程实现：
<table>
<tr><td>工程名</td><td>端口</td><td>作用</td></tr>
<tr><td>jwt-authserver</td><td>8080</td><td>授权服务器</td></tr>
<tr><td>jwt-resourceserver</td><td>8081</td><td>资源服务器</td></tr>
<table>

* 为了方便，使用内存模式，在内存中创建一个用户 user 密码 123456。

* 验证

请求令牌:

```text
curl -X POST --user 'clientapp:123' -d 'grant_type=password&username=user&password=123456' http://localhost:8080/oauth/token
```

返回JWT令牌:

```text
{
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NTQ0MzExMDgsInVzZXJfbmFtZSI6InVzZXIiLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiOGM0YWMyOTYtMDQwYS00Y2UzLTg5MTAtMWJmNjZkYTQwOTk3IiwiY2xpZW50X2lkIjoiY2xpZW50YXBwIiwic2NvcGUiOlsicmVhZCJdfQ.YAaSRN0iftmlR6Khz9UxNNEpHHn8zhZwlQrCUCPUmsU",
    "token_type": "bearer",
    "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ1c2VyIiwic2NvcGUiOlsicmVhZCJdLCJhdGkiOiI4YzRhYzI5Ni0wNDBhLTRjZTMtODkxMC0xYmY2NmRhNDA5OTciLCJleHAiOjE1NTY5Nzk5MDgsImF1dGhvcml0aWVzIjpbIlJPTEVfVVNFUiJdLCJqdGkiOiI0ZjA5M2ZjYS04NmM0LTQxZWUtODcxZS1kZTY2ZjFhOTI0NTAiLCJjbGllbnRfaWQiOiJjbGllbnRhcHAifQ.vvAE2LcqggBv8pxuqU6RKPX65bl7Zl9dfcoIbIQBLf4",
    "expires_in": 43199,
    "scope": "read",
    "jti": "8c4ac296-040a-4ce3-8910-1bf66da40997"
}
```

携带JWT令牌请求资源:

```text
curl -X POST -H "authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NTQ0MzExMDgsInVzZXJfbmFtZSI6InVzZXIiLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiOGM0YWMyOTYtMDQwYS00Y2UzLTg5MTAtMWJmNjZkYTQwOTk3IiwiY2xpZW50X2lkIjoiY2xpZW50YXBwIiwic2NvcGUiOlsicmVhZCJdfQ.YAaSRN0iftmlR6Khz9UxNNEpHHn8zhZwlQrCUCPUmsU" -d 'name=zhangsan' http://localhost:8081/api/hi
```

返回:

```text
hi , zhangsan
```

## 应用缓存的开发与设计

在 Spring 3.1 中引入了多 Cache 的支持，在 spring-context 包中定义了org.springframework.cache.Cache 和 org.springframework.cache.CacheManager 两个接口来统一不同的缓存技术。Cache 接口包含缓存的常用操作：增加、删除、读取等。CacheManager 是 Spring 各种缓存的抽象接口。
Spring 支持的常用 CacheManager 如下：

<table>
<tr><td>CacheManager</td><td>描述</td></tr>
<tr><td>SimpleCacheManager</td><td>使用简单的 Collection 来存储缓存</td></tr>
<tr><td>ConcurrentMapCacheManager</td><td>使用 java.util.ConcurrentHashMap 来实现缓存</td></tr>
<tr><td>NoOpCacheManager</td><td>仅测试用，不会实际存储缓存</td></tr>
<tr><td>EhCacheCacheManger</td><td>使用EhCache作为缓存技术。EhCache 是一个纯 Java 的进程内缓存框架，特点快速、精干，是 Hibernate 中默认的 CacheProvider，也是 Java 领域应用最为广泛的缓存</td></tr>
<tr><td>JCacheCacheManager</td><td>支持JCache（JSR-107）标准的实现作为缓存技术</td></tr>
<tr><td>CaffeineCacheManager</td><td>使用 Caffeine 作为缓存技术。用于取代 Guava 缓存技术</td></tr>
<tr><td>RedisCacheManager</td><td>使用Redis作为缓存技术</td></tr>
<tr><td>HazelcastCacheManager</td><td>使用Hazelcast作为缓存技术</td><</tr>
<tr><td>CompositeCacheManager</td><td>用于组合 CacheManager，可以从多个 CacheManager 中轮询得到相应的缓存</td></tr>
</table>

Spring Cache 提供了 @Cacheable 、@CachePut 、@CacheEvict 、@Caching 等注解，在方法上使用。通过注解 Cache 可以实现类似事务一样、缓存逻辑透明的应用到我们的业务代码上，且只需要更少的代码。
核心思想：当我们调用一个方法时会把该方法的参数和返回结果最为一个键值对存放在缓存中，等下次利用同样的参数来调用该方法时将不会再执行，而是直接从缓存中获取结果进行返回。

Cache注解

1. @EnableCaching
开启缓存功能，一般放在启动类上。
2. @CacheConfig
当我们需要缓存的地方越来越多，你可以使用@CacheConfig(cacheNames = {"cacheName"})注解在 class 之上来统一指定value的值，这时可省略value，如果你在你的方法依旧写上了value，那么依然以方法的value值为准。
3. @Cacheable
根据方法对其返回结果进行缓存，下次请求时，如果缓存存在，则直接读取缓存数据返回；如果缓存不存在，则执行方法，并把返回的结果存入缓存中。一般用在查询方法上。
查看源码，属性值如下：

<table>
<tr><td>属性/方法名</td><td>解释</td></tr>
<tr><td>value</td><td>缓存名，必填，它指定了你的缓存存放在哪块命名空间</td></tr>
<tr><td>cacheNames</td><td>与 value 差不多，二选一即可</td></tr>
<tr><td>key</td><td>可选属性，可以使用 SpEL 标签自定义缓存的key</td></tr>
<tr><td>keyGenerator</td><td>key的生成器。key/keyGenerator二选一使用</td></tr>
<tr><td>cacheManager</td><td>指定缓存管理器</td></tr>
<tr><td>cacheResolver</td><td>指定获取解析器</td></tr>
<tr><td>condition</td><td>条件符合则缓存</td></tr>
<tr><td>unless</td><td>条件符合则不缓存</td></tr>
<tr><td>sync</td><td>是否使用异步模式，默认为false</td></tr>
</table>

4. @CachePut
使用该注解标志的方法，每次都会执行，并将结果存入指定的缓存中。其他方法可以直接从响应的缓存中读取缓存数据，而不需要再去查询数据库。
一般用在新增方法上。 查看源码，属性值如下

<table>
<tr><td>属性/方法名</td><td>解释</td></tr>
<tr><td>value</td><td>缓存名，必填，它指定了你的缓存存放在哪块命名空间</td></tr>
<tr><td>cacheNames</td><td>与 value 差不多，二选一即可</td></tr>
<tr><td>key</td><td>可选属性，可以使用 SpEL 标签自定义缓存的key</td></tr>
<tr><td>keyGenerator</td><td>key的生成器。key/keyGenerator二选一使用</td></tr>
<tr><td>cacheManager</td><td>指定缓存管理器</td></tr>
<tr><td>cacheResolver</td><td>指定获取解析器</td></tr>
<tr><td>condition</td><td>条件符合则缓存</td></tr>
<tr><td>unless</td><td>条件符合则不缓存</td></tr>
</table>

5. @CacheEvict

使用该注解标志的方法，会清空指定的缓存。一般用在更新或者删除方法上 查看源码，属性值如下：

<table>
<tr><td>属性/方法名</td><td>解释</td></tr>
<tr><td>value</td><td>缓存名，必填，它指定了你的缓存存放在哪块命名空间</td></tr>
<tr><td>cacheNames</td><td>与 value 差不多，二选一即可</td></tr>
<tr><td>key</td><td>可选属性，可以使用 SpEL 标签自定义缓存的key</td></tr>
<tr><td>keyGenerator</td><td>key的生成器。key/keyGenerator二选一使用</td></tr>
<tr><td>cacheManager</td><td>指定缓存管理器</td></tr>
<tr><td>cacheResolver</td><td>指定获取解析器</td></tr>
<tr><td>condition</td><td>条件符合则缓存</td></tr>
<tr><td>allEntries</td><td>是否清空所有缓存，默认为 false。如果指定为 true，则方法调用后将立即清空所有的缓存</td></tr>
<tr><td>beforeInvocation</td><td>是否在方法执行前就清空，默认为 false。如果指定为 true，则在方法执行前就会清空缓存</td></tr>
</table>

6. @Caching

该注解可以实现同一个方法上同时使用多种注解。可从其源码看出

```java
public @interface Caching {

Cacheable[] cacheable() default {};

CachePut[] put() default {};

CacheEvict[] evict() default {};

}
```

* 集成 Spring Cache，只需要在 pom 中添加以下依赖：
  
```text
<!-- Spring Cache -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

* 开启缓存功能
在启动类上添加 @EnableCaching 注解，开启缓存功能

```java
@SpringBootApplication
@EnableCaching
public class SpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootApplication.class, args);
    }

}
```

### 整合EHCACHE

Ehcache是一种广泛使用的开源Java分布式缓存。主要面向通用缓存,Java EE和轻量级容器。它具有内存和磁盘存储，缓存加载器,缓存扩展,缓存异常处理程序,一个gzip缓存servlet过滤器,支持REST和SOAP api等特点。 

* 增加pom依赖

```text
<dependency>
<groupId>net.sf.ehcache</groupId>
<artifactId>ehcache</artifactId>
</dependency>

<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

* 在resources目录下新建ehcache.xml
  
```xml
<ehcache>

    <!--
        磁盘存储:将缓存中暂时不使用的对象,转移到硬盘,类似于Windows系统的虚拟内存
        path:指定在硬盘上存储对象的路径
        path可以配置的目录有：
            user.home（用户的家目录）
            user.dir（用户当前的工作目录）
            java.io.tmpdir（默认的临时目录）
            ehcache.disk.store.dir（ehcache的配置目录）
            绝对路径（如：d:\\ehcache）
        查看路径方法：String tmpDir = System.getProperty("java.io.tmpdir");
     -->
    <diskStore path="java.io.tmpdir" />

    <!--
        defaultCache:默认的缓存配置信息,如果不加特殊说明,则所有对象按照此配置项处理
        maxElementsInMemory:设置了缓存的上限,最多存储多少个记录对象
        eternal:代表对象是否永不过期 (指定true则下面两项配置需为0无限期)
        timeToIdleSeconds:最大的发呆时间 /秒
        timeToLiveSeconds:最大的存活时间 /秒
        overflowToDisk:是否允许对象被写入到磁盘
        说明：下列配置自缓存建立起600秒(10分钟)有效 。
        在有效的600秒(10分钟)内，如果连续120秒(2分钟)未访问缓存，则缓存失效。
        就算有访问，也只会存活600秒。
     -->
    <defaultCache maxElementsInMemory="10000" eternal="false"
                  timeToIdleSeconds="600" timeToLiveSeconds="600" overflowToDisk="true" />

    <cache name="myCache" maxElementsInMemory="10000" eternal="false"
                  timeToIdleSeconds="120" timeToLiveSeconds="600" overflowToDisk="true" />

</ehcache>
```

***参照例子chapter7-1***

### 整合Redis

Redis 优势

* 性能极高 – Redis能读的速度是110000次/s,写的速度是81000次/s 。
* 丰富的数据类型 – Redis支持二进制案例的 Strings, Lists, Hashes, Sets 及 Ordered Sets 数据类型操作。
* 原子 – Redis的所有操作都是原子性的，意思就是要么成功执行要么失败完全不执行。单个操作是原子性的。多个操作也支持事务，即原子性，通过MULTI和EXEC指令包起来。
* 丰富的特性 – Redis还支持 publish/subscribe, 通知, key 过期等等特性
  
* 就只需要这一个依赖！不需要spring-boot-starter-cache

```text
<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

当你导入这一个依赖时，SpringBoot的CacheManager就会使用RedisCache。

* 配置Redis

```properties
# Redis数据库索引（默认为0）
spring.redis.database=1
# Redis服务器地址
spring.redis.host=127.0.0.1
# Redis服务器连接端口
spring.redis.port=6379
# Redis服务器连接密码（默认为空）
spring.redis.password=
# 连接池最大连接数（使用负值表示没有限制）
spring.redis.pool.max-active=1000
# 连接池最大阻塞等待时间（使用负值表示没有限制）
spring.redis.pool.max-wait=-1
# 连接池中的最大空闲连接
spring.redis.pool.max-idle=10
# 连接池中的最小空闲连接
spring.redis.pool.min-idle=2
# 连接超时时间（毫秒）
spring.redis.timeout=0
```

* 模板编程

除了使用注解，我们还可以使用Redis模板。
Spring boot集成 Redis 客户端jedis。封装Redis 连接池，以及操作模板。

```java
@Autowired
    private StringRedisTemplate stringRedisTemplate;//操作key-value都是字符串

    @Autowired
    private RedisTemplate redisTemplate;//操作key-value都是对象

    /**
     *  Redis常见的五大数据类型：
     *  stringRedisTemplate.opsForValue();[String(字符串)]
     *  stringRedisTemplate.opsForList();[List(列表)]
     *  stringRedisTemplate.opsForSet();[Set(集合)]
     *  stringRedisTemplate.opsForHash();[Hash(散列)]
     *  stringRedisTemplate.opsForZSet();[ZSet(有序集合)]
     */
    public void test(){
        stringRedisTemplate.opsForValue().append("msg","hello");
    }
```

***redis 缓存代码参照chapter7-2***

## NoSQL的支持

NoSql是对于所有不使用关系作为数据管理的数据库系统的总称，NoSql的特点主要是不使用sql作为查询语言。

数据存储也不是固定的表和字段

NoSql数据库主要有文档存储型（MongoDB），图形关系存储型（Neo4j），键值对存储型（Redis）

### springboot使用NoSQL数据库MongoDB

MongoDB 是一个基于分布式文件存储的数据库。由 C++ 语言编写。旨在为 WEB 应用提供可扩展的高性能数据存储解决方案，官方解释。
在NoSql数据库中还是比较优秀的一款数据库，且官方网站现在已经逐步开始支持中文版了。

之前MySql介绍了Spring Data Jpa，对于MongoDB，Spring也提供了强大的支持Spring Data MongoDB，这个项目提供了与MongoDB文档数据库的集成。

* 添加mongodb依赖

```text
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

* 修改配置文件mongodb相关配置

```yaml
spring:
    data:
        mongodb:
            uri: mongodb://127.0.0.1:27017
            database: test
```

* 定义集合模型

在项目启动时候，以下定义的字段会对应到数据库中的数据结构，注意要添加@Document注解。

1. @Document: 标注于实体类上表明由mongo来维护该集合，默认集合名为类名还可手动指定集合名@Document(collection=user)
2. @Id: 主键，自带索引由mongo生成对应mongo中的_id字段(ObjectId)
3. @Indexed: 设置该字段索引，提高查询效率，设置参数(unique=true)为唯一索引，默认为false

* 创建继承于mongorepository的数据访问对象
  
继承MongoRepository，当然你也可以使用JpaRepository或MongoRepository，这些接口扩展了mongodb的CRUD操作的通用接口，此外还公开了底层持久化技术的功能，供我们扩展。

## 日志

Spring Boot在所有内部日志中使用Commons Logging，但是默认配置也提供了对常用日志的支持，如：Java Util Logging，Log4J, Log4J2和Logback。每种Logger都可以通过配置使用控制台或者文件输出日志内容。Spring Boot 默认使用Logback作为日志记录工具。日志默认输出到控制台但也能输出到文件中。我们通过spring-boot-starter-logging 加入Logback依赖，其实只要我们加入任意的Spring Boot starter 都会默认引入spring-boot-starter-logging，因此 我们不需要分开加入他们。

* 日志级别从低到高分：

```text
TRACE < DEBUG < INFO < WARN < ERROR < FATAL
```

* 默认格式
默认的日志输出如下：

```text
2016-04-13 08:23:50.120  INFO 37397 --- [main] org.hibernate.Version : HHH000412: Hibernate Core {4.3.11.Final}
```

输出内容元素具体如下：

1. 时间日期 — 精确到毫秒
2. 日志级别 — ERROR, WARN, INFO, DEBUG or TRACE
3. 进程ID
4. 分隔符 — --- 标识实际日志的开始
5. 线程名 — 方括号括起来（可能会截断控制台输出）
6. Logger名 — 通常使用源代码的类名
7. 日志内容

* 控制台输出
在Spring Boot中默认配置了ERROR、WARN和INFO级别的日志输出到控制台。
可以通过两种方式切换至DEBUG级别：

1. 在运行命令后加入--debug标志，如：$ java -jar myapp.jar --debug
2. 在application.properties中配置debug=true，该属性置为true的时候，核心Logger（包含嵌入式容器、hibernate、spring）会输出更多内容，但是你自己应用的日志并不会输出为DEBUG级别。

* 多彩输出
如果你的终端支持ANSI，设置彩色输出会让日志更具可读性。通过在application.properties中设置spring.output.ansi.enabled参数来支持。

1. NEVER：禁用ANSI-colored输出（默认项）
2. DETECT：会检查终端是否支持ANSI，是的话就采用彩色输出（推荐项）
3. ALWAYS：总是使用ANSI-colored格式输出，若终端不支持的时候，会有很多干扰信息，不推荐使用

* 文件输出

Spring Boot默认配置只会输出到控制台，并不会记录到文件中，但是我们通常生产环境使用时都需要以文件方式记录。

若要增加文件输出，需要在application.properties中配置logging.file或logging.path属性。

1. logging.file，设置文件，可以是绝对路径，也可以是相对路径。如：logging.file=my.log
2. logging.path，设置目录，会在该目录下创建spring.log文件，并写入日志内容，如：logging.path=/var/log
日志文件会在10Mb大小的时候被截断，产生新的日志文件，默认级别为：ERROR、WARN、INFO

* 级别控制
在Spring Boot中只需要在application.properties中进行配置完成日志记录的级别控制。

配置格式：logging.level.*=LEVEL

```property
logging.level.com.hu=DEBUG：com.hu包下所有class以DEBUG级别输出
logging.level.root=WARN：root日志以WARN级别输出
```

### 自定义日志配置

由于日志服务一般都在ApplicationContext创建前就初始化了，它并不是必须通过Spring的配置文件控制。因此通过系统属性和传统的Spring Boot外部配置文件依然可以很好的支持日志控制和管理。

根据不同的日志系统，你可以按如下规则组织配置文件名，就能被正确加载：

1. Logback：logback-spring.xml, logback-spring.groovy, logback.xml, logback.groovy
2. Log4j：log4j-spring.properties, log4j-spring.xml, log4j.properties, log4j.xml
3. Log4j2：log4j2-spring.xml, log4j2.xml
4. JDK (Java Util Logging)：logging.properties
Spring Boot官方推荐优先使用带有-spring的文件名作为你的日志配置（如使用logback-spring.xml，而不是logback.xml）

* 自定义输出格式

在Spring Boot中可以通过在application.properties配置如下参数控制输出格式：

1. logging.pattern.console：定义输出到控制台的样式（不支持JDK Logger）
2. logging.pattern.file：定义输出到文件的样式（不支持JDK Logger）

#### 日志logback

Logback是由log4j创始人设计的又一个开源日志组件。

logback当前分成三个模块：logback-core,logback- classic和logback-access。l

ogback-core是其它两个模块的基础模块。logback-classic是log4j的一个 改良版本。
此外logback-classic完整实现SLF4J API使你可以很方便地更换成其它日志系统如log4j或JDK14 Logging。

logback-access访问模块与Servlet容器集成提供通过Http来访问日志的功能。 

Logback是要与SLF4J结合起来用

* Logback的配置介绍

1. Logger、appender及layout

  Logger作为日志的记录器，把它关联到应用的对应的context上后，主要用于存放日志对象，也可以定义日志类型、级别。

  Appender主要用于指定日志输出的目的地，目的地可以是控制台、文件、远程套接字服务器、 MySQL、 PostreSQL、 Oracle和其他数据库、JMS和远程UNIX Syslog守护进程等。

  Layout 负责把事件转换成字符串，格式化的日志信息的输出。
2. logger context

  各个logger 都被关联到一个 LoggerContext，LoggerContext负责制造logger，也负责以树结构排列各 logger。其他所有logger也通过org.slf4j.LoggerFactory 类的静态方法getLogger取得。 getLogger方法以 logger 名称为参数。用同一名字调用LoggerFactory.getLogger 方法所得到的永远都是同一个logger对象的引用。
3. 有效级别及级别的继承

  Logger 可以被分配级别。级别包括：TRACE、DEBUG、INFO、WARN 和 ERROR，定义于 ch.qos.logback.classic.Level类。如果logger没有被分配级别，那么它将从有被分配级别的最近的祖先那里继承级别。root logger 默认级别是 DEBUG。
4. 打印方法与基本的选择规则

  打印方法决定记录请求的级别。例如，如果 L 是一个 logger 实例，那么，语句 L.info("..")是一条级别为 INFO 的记录语句。记录请求的级别在高于或等于其 logger 的有效级别时被称为被启用，否则，称为被禁用。记录请求级别为 p，其 logger的有效级别为 q，只有则当p>=q时，该请求才会被执行。

在src/main/resources下面添加logback-spring.xml，内容如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--
    Copyright 2010-2011 The myBatis Team
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
        http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
-->
<configuration>
    <!--======================================= 本地变量 ========================================-->

    <!-- 应用名称：和统一配置中的项目代码保持一致（小写） -->
    <property name="LOG_HOME" value="./logs"/>
    <property name="APP_NAME" value="SpringLogbackExampleApplication"/>
    <!-- 日志文件保留天数-->
    <property name="LOG_MAX_HISTORY" value="30"/>
    <!-- 定义日志文件的存储地址 勿在 LogBack 的配置中使用相对路径 -->
    <!-- 应用日志文件保存路径 -->
    <property name="LOG_APP_HOME" value="${LOG_HOME}/${APP_NAME}/app"/>
    <!-- 系统日志文件保存路径  -->
    <property name="LOG_SYS_HOME" value="${LOG_HOME}/${APP_NAME}/sys"/>

    <!-- 控制台输出 -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
        </encoder>
    </appender>
    <appender name="APP" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_APP_HOME}/project.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <FileNamePattern>${LOG_APP_HOME}/project.log.%d{yyyy-MM-dd}.log</FileNamePattern>
            <MaxHistory>${LOG_MAX_HISTORY}</MaxHistory>
        </rollingPolicy>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %X{applicationName} %X{IP} [%thread] %-5level %logger{50} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 为了防止开启log4jdbc 之后,导致日志的激增,默认关闭某些logger。
            log4jdbc.sqltiming 计时统计SQL的执行用时,OFF/ERROR/WARN/INFO:彻底关闭，,DEBUG:输出所有的SQL时间(根据耗时有DEBUG/WARN/ERROR不同级别),TRACE:输出调用时堆栈信息。
            log4jdbc.debug 为WARN或者以上级别时，没有信息输出，INFO时仅输出启动时的配置信息，DEBUG时输出详细的DEBUG信息 -->
    <logger name="log4jdbc.sqltiming">
        <level value="TRACE" />
    </logger>

    <logger name="log4jdbc.debug">
        <level value="TRACE" />
    </logger>

    <logger name="com.mchange.v2.c3p0">
        <level value="ERROR" />
    </logger>

    <logger name="javax.servlet">
        <level value="ERROR" />
    </logger>

    <logger name="org.springframework.web.client">
        <level value="ERROR" />
    </logger>

    <!--===============================  日志输出: 默认主业务日志 ======================================-->
    <root level="info">
        <!--线上换成APP-->
        <appender-ref ref="APP"/>
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```

## 异步消息

目前主流的消息中间件有activemq，rabbitmq，rocketmq，kafka，我们要根据实际的业务场景来选择一款合适的消息中间件，关注的主要指标有，消息投递的可靠性，可维护性，吞吐量以及中间件的特色等重要指标来选择，大数据领域肯定是kafka，那么传统的业务场景就是解耦，异步，削峰。那么就在剩下的3款产品中选择一款，从吞吐量，社区的活跃度，消息的可靠性出发，一般的中小型公司选择rabbitmq来说可能更为合适.

### RabbitMQ

RabbitMQ是一个开源的AMQP实现，服务器端用Erlang语言编写，支持多种客户端，如：Python、Ruby、.NET、Java、JMS、C、PHP、ActionScript、XMPP、STOMP等，支持AJAX。用于在分布式系统中存储转发消息，在易用性、扩展性、高可用性等方面表现不俗。

AMQP，即Advanced message Queuing Protocol，高级消息队列协议，是应用层协议的一个开放标准，为面向消息的中间件设计。消息中间件主要用于组件之间的解耦，消息的发送者无需知道消息使用者的存在，反之亦然。

AMQP的主要特征是面向消息、队列、路由（包括点对点和发布/订阅）、可靠性、安全。

RabbitMQ是一个开源的AMQP实现，服务器端用Erlang语言编写，支持多种客户端，如：Python、Ruby、.NET、Java、JMS、C、PHP、ActionScript、XMPP、STOMP等，支持AJAX。用于在分布式系统中存储转发消息，在易用性、扩展性、高可用性等方面表现不俗。

![rabbitmqSetupComponent](./springboot企业级开发/imgs/rabbitmqSetupComponent.png)
![amqpConcepts](./springboot企业级开发/imgs/amqpConcepts.png)
![ampqExchange](./springboot企业级开发/imgs/ampqExchange.png)
![rabbitMQUI](./springboot企业级开发/imgs/rabbitMQUI.png)
![rabbitMQMonitor](./springboot企业级开发/imgs/rabbitMQMonitor.png)

* ACK确认机制

1. 消息发送确认
ConfirmCallback  只确认消息是否正确到达 Exchange 中。

ReturnCallback   消息没有正确到达队列时触发回调，如果正确到达队列不执行。

2. 消息接收确认

* AcknowledgeMode.NONE：自动确认
* AcknowledgeMode.AUTO：根据情况确认
* AcknowledgeMode.MANUAL：手动确认

***rabbitMQ样例参照chapter10-1***

### Kakfa

多数情况下都是把 Kafka 作为一个分布式消息队列来使用的，分布式消息队列可以提供应用解耦、流量消峰、消息分发等功能，已经是大型互联网服务架构不可缺少的基础设置了。
![kafkaSetupComponent](./springboot企业级开发/imgs/kafkaSetupComponent.png)

* topic 和 partition

Kafka 对数据提供的核心抽象，topic 是发布的数据流的类别或名称。topic 在 Kafka 中，支持多订阅者； 也就是说，topic 可以有零个、一个或多个消费者订阅写到相应 
topic 的数据。对应每一个 topic，Kafka 集群会维护像一个如下这样的分区的日志.

每个 Partition 都是一个有序的、不可变的并且不断被附加的记录序列，也就是一个结构化提交日志（commit log）。为了保证唯一标性识 Partition 中的每个数据记录，Partition 中的记录每个都会被分配一个叫做偏移（offset）顺序的ID号。通过一个可配置的保留期，Kafka 集群会保留所有被发布的数据，不管它们是不是已经被消费者处理。例如，如果保留期设置为两天，则在发布记录后的两天内，数据都可以被消费，之后它将被丢弃以释放空间。 Kafka 的性能是不为因为数据量大小而受影响的，因此长时间存储数据并不成问题。

事实上，在每个消费者上保留的唯一元数据是消费者在日志中的偏移位置，这个偏移由消费者控制：通常消费者会在读取记录时线性地提高其偏移值（offset++），但实际上，由于偏移位置由消费者控制，它可以以任何顺序来处理数据记录。 例如，消费者可以重置为较旧的偏移量以重新处理来自过去的数据，或者跳过之前的记录，并从“现在”开始消费。 这种特征的组合意味着 Kafka 消费者非常轻量级，随意的开启和关闭并不会对其他的消费者有大的影响。
![topicAndPartition](./springboot企业级开发/imgs/topicAndPartition.png)

* Kafka 作为消息系统
消息系统传统上有两种模式: 队列和发布-订阅。

1. 队列模式中，消费者池可以从服务器读取，每条记录只会被某一个消费者消费

允许在多个消费者实例上分配数据处理，但是一旦数据被消费之后，数据就没有了

2. 发布订阅模式中，记录将广播给所有消费者

允许将数据广播到多个进程，但无法缩放和扩容，因为每个消息都发送给每个订阅用户

* 生产者
生产者将数据发布到他们选择的 topic ， 生产者负责选择要吧数据分配给 topic 中哪个 Partition。这可以通过循环方式（round-robin）简单地平衡负载，或者可以根据某些语义进行分区（例如基于数据中的某些关键字）来完成。

* 消费者

消费者们使用消费群组(consumer group )名称来标注自己，几个消费者共享一个 group，每一个发布到 topic 的数据会被传递到每个消费群组(consumer group )中的一个消费者实例。 消费者实例可以在不同的进程中或不同的机器上。
如果所有的消费者实例具有相同的 consumer group，则记录将在所有的消费者实例上有效地负载平衡
如果所有的消费者实例都有不同的 consumer group，那么每个记录将被广播给所有的消费者进程，每个数据都发到了所有的消费者。
![consumerGroup](./springboot企业级开发/imgs/consumerGroup.png)

***kafka样例参照chapter10-2***
