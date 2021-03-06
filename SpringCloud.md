## 什么是服务治理
 Spring Cloud 封装了Netflix公司开发的Eureka模块来实现服务治理
 
 在传统的**rpc**远程调用框架中，管理每个服务与服务之间依赖关系比较复杂，管理比较复杂，所以需要使用服务治理
 ，管理服务于服务之间的依赖关系，可以实现服务调用，负载均衡，容错等，实现服务发现与注册。
 

## 什么是服务注册与发现
Eureka采用CS的设计框架，EurekaServer作为服务注册功能的服务器，它是服务注册中心。而系统中的其他微服务，
使用Eureka的客户端连接到EurekaServer并维持心跳连接。这样系统的维护人员就可以通过EurekaServer来监控
系统中的各个微服务是否正常运行。
在服务注册与发现中，有一个注册中心。当服务启动的时候，会把当前自己服务器的信息，比如服务地址通讯地址等
以别名方式注册到注册中心上，另一方(消费者|服务提供者)，以该别名的方式去注册中心上获取到实际的服务通讯地址
，然后在实现本地**rpc**调用**rpc**远程调用框架核心设计思想：在于注册中心，因为使用注册中心管理每个服务于
服务之间的一个依赖关系(服务治理概念)。在任何**rpc**远程框架中，都会有一个注册中心(存放服务地址相关信息[接口地址]) 

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200710234801615.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)

## Eureka包含两个组件：Eureka Sever 和Eureka Client
- EurekaServer提供服务注册服务
   各个微服务节点通过配置启动后，会在EurekaServer中进行注册，这样EurekaServer中的服务注册表中将会存储
   所有可用服务节点的信息，服务节点的信息可以在界面中直观看到
- EurekaClient通过注册中心进行访问
   是一个Java客户端，用于简化EurekaServer的交互，客户端同时也具备一个内置的，使用轮询负载算法的
   负载均衡器。在应用启动后，将会向EurekaServer发送心跳(默认周期为30s)。如果EurekaServer在多个
   心跳周期内没有接受到某个节点的心跳，EurekaServer将会从服务注册表中把这个服务节点移除(默认90s)
   
 ## 微服务注册名称配置说明
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/20200711001237999.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)
 
 
 ## 自我保护机制
 
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/20200711001348572.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)
 
- 故障现象：保护模式主要用于一组客户端和Eureka Server之间存在网络分区场景下的保护，一旦

进入保护模式，Eureka Server将会尝试保护其服务注册表中的信息，不再删除服务注册表中的数据，

也就是不会注销任何微服务。

- 导致原因：某时刻某一个微服务不可用了，Eureka不会立即清除，依旧会对微服务的信息进行保存

属于CAP里面的AP分支
       
- 什么是自我保护模式
默认情况下，如果Eureka Server在一定时间内没有接受到某个微服务实例的心跳，EurekaServer将

会注销该实例(默认90s)。但是是当网络分区故障发生(延时，卡顿，拥挤)时，微服务与EurekaServer

之间无法正常通信，以上行为可能变的非常危险--因为微服务本身其实是健康的，此时不应该注销这个

微服务。Eureka通过“自我保护模式”来解决这个问题---当EurekaServer节点在短时间内丢失过多客户

端时(可能发生了网络分区故障)，那么这个节点就会进入自我保护模式。 

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200712083645748.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)

- 关闭自我保护机制
    
 ```yml
 修改Eureka的配置
     #关闭自我保护机制，保证不可用服务被及时剔除
            eureka:
               server:
                 enable-self-preservation: false   #默认是true，开启的
                 eviction-interval-timer-in-ms: 2000   #将时间缩短
 ```      
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/20200712085241686.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)
       
```yml
修改服务端的配置

#默认
 eureka.instance.lease-renewal-interval-in-seconds=30 #单位为秒(默认为30s)
 eureka.instance.lease-expiration-duration-in-seconds=90 #单位为秒(默认为30s)
  
#修改
 #Eureka客户端服务端发送心跳的时间间隔，单位为秒(默认为30s)
 eureka.instance.lease-renewal-interval-in-seconds=1
 #Eureka服务端在收到最后一次心跳等待上限，单位为秒(默认为90s)，超时将剔除 
 eureka.instance.lease-expiration-duration-in-seconds=2
```

## Eureka集群原理说明
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200711143436580.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)

问题：微服务RPC远程服务调用最核心的是什么？
  高可用，试想你的注册中心只有一个，它出了故障，会导致整个微服务环境不可用。
  
解决方式：搭建Eureka 注册中心集群，实现负载均衡+故障容错         


---
## Consul 简介

 - 官网：https://www.consul.io/intro/index.html
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/20200714181234442.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)
 
 - 能干什么
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/20200714181542821.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)
 
 - 下载：https://www.consul.io/downloads.html
 
 - 中文官网：https://www.springcloud.cc/spring-cloud-consul.html
 
 - 官网安装说明：https://learn.hashicorp.com/consul/getting-started/install.html
 
 - 下载完成后只有一个consul.exe文件，硬盘路径下双击运行，查看版本号信息
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/20200714182704351.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)
 
 
 - 使用开发模式启动：1.consul agent -dev 2.通过以下地址可以访问Consul的首页：http://localhost:8500
 
 3.界面
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/20200714212201439.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)
 
 
 ## 三种注册中心的异同点
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/20200714223140139.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/20200714223406412.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)



## Ribbon(负载均衡+RestTemplate)

- 是什么

Spring Cloud Ribbon 是基于Netflix Ribbon实现的一套客户端 负载均衡的工具

简单的说，Ribbon就是Netflix发布的开源项目，主要功能是提供客户端的软件负载均衡算法和服务调用。Ribbon客户端组件提供一系列
完善的配置项如连接超时，重试等。简单的说，就是在配置文件中列出Load Balancer(简称LB)后面所有的机器，Robbin会自动的帮助你
基于某种规则(如轮询，随机连接等)去连接这些机器。我们很容易使用Robbin实现自定义的负载均衡算法。

- 官网：https://github.com/Netflix/ribbon/wiki/Getting-Started

- 能干什么？

  1.LB(负载均衡)
  
      LB负载均衡(locad Balance)
      简单的说就是将用户的请求分摊到多个服务上，从而达到系统的HA(高可用)。
      常见的负载均衡有软件Nginx,LVS,硬件F5等
      
      Ribbon本地负载均衡客户端 VS Nginx服务端负载均衡区别：
      Nginx是服务器负载均衡，客户端所有请求都会交给Nginx，然后由Nginx实现
      转发请求，即负载均衡是由服务端实现的
      Ribbon本地负载均衡，在调用微服务接口的时候，会在注册中心上获取注册信
      息服务列表之后缓存到JVM本地，从而在本地实现RPC远程服务调用技术
      
     * 集中式LB
     
     * 进程内LB
- 架构说明
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200718153213733.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)

Ribbon在工作时分成两部
第一步先选择EurekaServer，他优先选择在同一个区域负载较少的Server

第二步再根据用户指定的策略，在从server取到的服务注册列表中选择一个地址

其中Ribbon提供了多种策略，比如轮询，随机和根据相应时间加权。     

- Ribbon核心组件IRule(IRule:根据特定算法中从服务列表中选取一个要访问的服务)
![在这里插入图片描述](https://img-blog.csdnimg.cn/2020071816124226.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200718161433692.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)

- 注意配置细节

      官方文档明确给出了警告：
      这个自定义配置类不能放在@ComponentScan所扫描的当前包下以及子包下
      否则我们自定义的这个配置类就会被所有的Ribbon客户端所共享，达不到特殊定制的目的了
      
- 默认负载轮询算法原理
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200718193505665.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)   



## OpenFeign

![在这里插入图片描述](https://img-blog.csdnimg.cn/202007191158061.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)

- OpenFeign超时控制：
    
    OpenFeign默认等待1秒钟，超过后报错
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200719200054400.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)
 
      默认Feign客户端只等待一秒钟，但是服务端处理需要超过1秒钟，导致Feign客户端不想等待，直接报错。
      为了避免这样的情况，有时候我们需要设置Feign客户端的超时控制。
      
      yml文件中开启配置
            
```yml
#设置feign客户端超时时间(OpenFeign默认支持ribbo)
ribbon:
#指建立连接后从服务器读取到可用资源所用的时间
   ReadTimeout: 5000
#指的是建立连接所用的时间，适用于网络状况正常情况下，两端连接所用时间   
   ConnectTimeout: 5000
```

- OpenFeign日志打印功能

      1. 日志打印功能：
      Feign提供了日志打印功能，我们可用通过配置来调整日志级别，从而了解Feign
      请求的细节，说白了就是对Feign接口的调用情况进行监控和输出。
      2.配置日志bean
 ```java
 @Configuration
 public class FeignConfig{
    
    @Bean
    Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }
 }
 ```
     3.yml文件需要开启日志的Feign客户端
 ```yml
     logging:
       level:
         #feign日志以什么级别监控哪个接口
         接口路径：日志级别
 ```    
 
##Hystrix

- 分布式系统面临的问题
   
   **复杂分布式体系结构中的应用程序有数十个依赖关系，每个依赖关系在某些时候将不可避免的失败**
   
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200721220925763.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)

服务雪崩：
 多个微服务之间调用的时候，假设微服务A调用微服务B和微服务C，微服务B和微服务C又调用其他的微服务，这就是所谓的“扇出”。
 如果扇出的链路上某个微服务的调用响应时间过长或者不可用，对微服务A的调用就会占用越来越多的系统资源，进而引起系统崩溃，
 所谓的“雪崩效应”。
 
 对于高流量的应用来说，单一的后端依赖可能会导致所有的服务器上的资源都在几秒内饱和。比失败更糟糕的是，这些应用程序还
 可能导致服务之间的延迟增加，备份队列，线程和其他系统资源紧张，导致整个系统发生更多的连级故障。这些都表示需要对故障
 和延时进行隔离和管理，以便单个依赖关系的失败，不能取消整个应用程序或系统。
 
 所以，通常当你发现一个模块下的某个实例失败后，这个时候这个模块依然还会接收流量，然后这个问题的模块还调用了其他的模块
 ，这样就会发生连级故障，或者叫雪崩。
 
- Hystrix是什么
Hystrix是一个用于处理分布式系统的延迟和容错的开源库，在分布式系统里，许多依赖不可避免的会调用失败，比如超时，异常等，
Hystrix能够保证在一个依赖出现问题的情况下，不会导致整体服务失败，避免级联故障，以提高分布式系统的弹性。

“断路器”本身是一种开关装置，当某个服务单元发生故障之后，通过断路器的故障监控(类似于熔断保险丝)，向调用方返回一个符合
预期的，可处理的备选响应(FallBack)，而不是长时间的等待或者抛出调用方无法处理的异常，这样就保证了服务调用方的线程不会
被长时间，不必要的占用，从而避免了故障在分布式系统中的蔓延，乃至雪崩。

- 能干啥

 1.服务降级
 
 2.服务熔断
 
 3.接近实时的监控
 
- 官网：https://github.com/Netflix/Hystrix/wiki/How-To-Use

- Hystrix重要概念
  * 服务降级：服务器忙，请稍后再试，不让客户端等待并立刻返回一个友好的提示，fallback
  
    哪些情况会触发降级：程序运行异常，超时，服务熔断触发服务降级，线程池/信号量打满也会导致服务降级        
    
   * 服务熔断：类似于保险丝达到最大服务访问后，直接拒绝访问，拉闸限流，然后调用服务降级的方法并返回友好的提示
  
   * 服务限流：秒杀高并发等操作，严禁一窝蜂的过来拥挤，大家排队，一秒N个，有序运行
  
     
 
          