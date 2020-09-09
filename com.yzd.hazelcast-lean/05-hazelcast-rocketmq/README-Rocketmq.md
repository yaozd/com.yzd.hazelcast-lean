# rocketMQ

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