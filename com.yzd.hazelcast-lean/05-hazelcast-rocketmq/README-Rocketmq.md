# rocketMQ

## 入门
- [RocketMQ入门到入土（一）新手也能看懂的原理和实战！](https://www.debug8.com/java/t_54999.html)
- [win10 RocketMQ的简单运用](https://www.cnblogs.com/minblog/p/13328874.html)
- [参数说明](https://blog.csdn.net/qq_32711825/article/details/78579864)

## 关键词解析
- [NameServer](https://www.jianshu.com/p/3d8d594d9161)
    ```
    部署：Namesrv可集群部署，但集群间不同步数据
    Name Server是一个几乎无状态节点，可集群部署，节点之间无任何信息同步
    NameServer的主要功能是为整个MQ集群提供服务协调与治理，
    具体就是记录维护Topic、Broker的信息，及监控Broker的运行状态。
    为client提供路由能力（具体实现和zk有区别，NameServer是没有leader和follower区别的，不进行数据同步，通过Broker轮训修改信息）
    ```
- [Broker](https://blog.csdn.net/Yooneep/article/details/88844359)
    ```
    部署：Broker支持主/从模式的集群模式
    Broker是RocketMQ的核心，大部分工作都在Broker中完成，
    包括接收请求，处理消费，消费持久，消息的HA，以及服务端过滤等都在里面完成
    ```
- 消息模式
    - 广播消费(一条消息被多个 Consumer 消费,在广播消费中的 Consumer Group 概念可以认为在消息划分方面无意义)
    - 集群消费(一个 Consumer Group 中的 Consumer 实例平均分摊消费消息。)
- [Topic，Topic分片和Queue](https://blog.csdn.net/qq_34930488/article/details/101282436)
    ```
    一个Broker=>多个Topic=>多个Queue
    ```
- 消息ACK机制
    - [RocketMQ——消息ACK机制及消费进度管理](https://blog.csdn.net/linuxheik/article/details/79579329)
    - [rocketmq——关于消费的疑惑（ACK机制）](https://blog.csdn.net/qq_35362055/article/details/81560388)
    - []()
    - []()
    - []()
- RocketMQ批量消费、消息重试、消费模式、刷盘方式
    - [RocketMQ批量消费、消息重试、消费模式、刷盘方式](https://blog.csdn.net/u010634288/article/details/56049305)
- 消息消费管理
    - [RocketMQ4.0源码分析之-消息消费管理](https://blog.csdn.net/binzhaomobile/article/details/75004190)
        ```
        幂等
        广播消费vs. 集群消费
        拉消息vs. 推消息
        顺序消费vs. 并行消费
        ```
- consumer优雅停机
    - [rocketmq consumer优雅停机的探索](https://www.jianshu.com/p/676890f09a05)
    ```
    MQ_RUN信号变量,假设叫MQ_RUN=1，当rocketmq消费消息时读取该变量，判断是MQ_RUN==1，成立则继续执行
    ```
## 事务消息
- [分布式事务之 RocketMQ 事务消息详解](https://zhuanlan.zhihu.com/p/108751293) 
- 两个相关的概念
    - [Half(Prepare) Message——半消息(预处理消息)](https://zhuanlan.zhihu.com/p/108751293)
    - [Message Status Check——消息状态回查](https://zhuanlan.zhihu.com/p/108751293)
- 具体流程进行分析
```
Step1：Producer向Broker端发送Half Message；
Step2：Broker ACK，Half Message发送成功；
Step3：Producer执行本地事务；
Step4：本地事务完毕，根据事务的状态，Producer向Broker发送二次确认消息，确认该Half Message的Commit或者Rollback状态。
        Broker收到二次确认消息后，对于Commit状态，则直接发送到Consumer端执行消费逻辑，
        而对于Rollback则直接标记为失败，一段时间后清除，并不会发给Consumer。正常情况下，到此分布式事务已经完成，
        剩下要处理的就是超时问题，即一段时间后Broker仍没有收到Producer的二次确认消息；
Step5：针对超时状态，Broker主动向Producer发起消息回查；
Step6：Producer处理回查消息，返回对应的本地事务的执行结果；
Step7：Broker针对回查消息的结果，执行Commit或Rollback操作，同Step4。
```

## 配置参考
- [SpringBoot2.0 整合 RocketMQ ,实现请求异步处理](https://mp.weixin.qq.com/s/uF29K8gzv7qHYk-K2pQkpQ)
- [https://github.com/cicadasmile/middle-ware-parent](https://github.com/cicadasmile/middle-ware-parent)
```
rocketmq:
  # 生产者配置
  producer:
    isOnOff: on
    # 发送同一类消息的设置为同一个group，保证唯一
    groupName: FeePlatGroup
    # 服务地址
    namesrvAddr: 10.1.1.207:9876
    # 消息最大长度 默认1024*4(4M)
    maxMessageSize: 4096
    # 发送消息超时时间,默认3000
    sendMsgTimeout: 3000
    # 发送消息失败重试次数，默认2
    retryTimesWhenSendFailed: 2
  # 消费者配置
  consumer:
    isOnOff: on
    # 官方建议：确保同一组中的每个消费者订阅相同的主题。
    groupName: FeePlatGroup
    # 服务地址
    namesrvAddr: 10.1.1.207:9876
    # 接收该 Topic 下所有 Tag
    topics: FeePlatTopic~*;
    consumeThreadMin: 20
    consumeThreadMax: 64
    # 设置一次消费消息的条数，默认为1条
    consumeMessageBatchMaxSize: 1
 
# 配置 Group  Topic  Tag
fee-plat:
  fee-plat-group: FeePlatGroup
  fee-plat-topic: FeePlatTopic
  fee-account-tag: FeeAccountTag
``` 
- 
## RocketMQ设计理念和目标
- [RocketMQ技术内幕学习笔记](https://blog.csdn.net/dezhonger/article/details/96387459)
- [RocketMQ技术内幕 PDF 下载](http://www.java1234.com/a/javabook/javaweb/2019/0303/13055.html)
````
设计理念:S
    NameServer集群之间互不通信，topic路由不保持强一致，追求最终一致，相比ZK有极大提升
    高效的IO存储机制
    容忍存在设计缺陷
设计目标:
    架构模式
    顺序消息
    消息过滤
    消息存储
    消息高可用性
    消息到达(消费)低延迟
    确保消息必须被消费一次
    回溯消息
    消息堆积
    定时消息
    消息重试机制
````


## 安装
- windows
- [window 中安装rocketMq和rocketMq-console](https://blog.csdn.net/junge1545/article/details/89922704)
```
注：必须在DOS命令窗口下执行，不可在powershell窗口下执行
1.
下载地址
https://www.apache.org/dyn/closer.cgi?path=rocketmq/4.7.1/rocketmq-all-4.7.1-bin-release.zip
2.
系统环境变量配置
变量名：ROCKETMQ_HOME
3.
启动NAMESERVER
cmd命令框执行进入至‘MQ文件夹\bin’下，然后执行
start mqnamesrv.cmd
4.
启动BROKER
cmd命令框执行进入至‘MQ文件夹\bin’下，然后执行
start mqbroker.cmd -n 127.0.0.1:9876 autoCreateTopicEnable=true
启动增加配置文件broker.conf
start mqbroker.cmd -n 127.0.0.1:9876 autoCreateTopicEnable=true -c ../conf/broker.conf
5.控制台
下载地址：
https://github.com/apache/rocketmq-externals.git
打包：
进入‘\rocketmq-externals\rocketmq-console’文件夹，执行
mvn clean package -Dmaven.test.skip=true
PS:可能需要调整maven仓库
启动：
java -jar rocketmq-console-ng-1.0.1.jar
测试：
http://127.0.0.1:8080/
```
- broker.conf
```
brokerClusterName = DefaultCluster
brokerName = broker-a
brokerId = 0
deleteWhen = 04
fileReservedTime = 48
brokerRole = ASYNC_MASTER
flushDiskType = ASYNC_FLUSH
# RocketMQ可在broker.conf文件中配置Consumer端的重试次数和重试时间间隔，如下：
messageDelayLevel = 1s 1s 1s 1s 1s 1s 1s 2s 3s 4s 5s
```
- [Win10在当前目录快速打开cmd的方法](https://www.cnblogs.com/yizhilin/p/12975052.html)
- [win 下 dos查看和设置环境变量](https://jingyan.baidu.com/article/574c5219053a926c8d9dc1ed.html)
```
# 设置临时环境变量：
1.
echo %ROCKETMQ_HOME%
2.
设置环境变量ROCKETMQ_HOME (当前窗口有效)
set ROCKETMQ_HOME=xxx
3.
确认
echo %ROCKETMQ_HOME%
```

- maven仓库
```
<project>
<repositories>
    <repository>
        <id>alimaven</id>
        <url>https://maven.aliyun.com/repository/public</url>
    </repository>
</repositories>
 
<pluginRepositories>
    <pluginRepository>
        <id>alimaven</id>
        <url>https://maven.aliyun.com/repository/public</url>
    </pluginRepository>
</pluginRepositories>
</project>
```