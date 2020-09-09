# rocketMQ

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
- 

## 安装
- windows
- [window 中安装rocketMq和rocketMq-console](https://blog.csdn.net/junge1545/article/details/89922704)
```
1.
下载地址
https://www.apache.org/dyn/closer.cgi?path=rocketmq/4.7.1/rocketmq-all-4.7.1-bin-release.zip
2.
系统环境变量配置
变量名：ROCKETMQ_HOME
3.
启动NAMESERVER
cmd命令框执行进入至‘MQ文件夹\bin’下，然后执行‘start mqnamesrv.cmd’
4.
启动BROKER
cmd命令框执行进入至‘MQ文件夹\bin’下，然后执行‘start mqbroker.cmd -n 127.0.0.1:9876 autoCreateTopicEnable=true’
5.控制台
下载地址：
https://github.com/apache/rocketmq-externals.git
打包：
进入‘\rocketmq-externals\rocketmq-console’文件夹，执行‘mvn clean package -Dmaven.test.skip=true’
PS:可能需要调整maven仓库
启动：
java -jar rocketmq-console-ng-1.0.1.jar
测试：
http://127.0.0.1:8080/
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