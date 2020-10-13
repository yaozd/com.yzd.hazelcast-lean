# 重试机制
- [RocketMQ详解(12)——RocketMQ的重试机制](https://blog.csdn.net/weixin_34452850/article/details/82746852)
- [跟我学RocketMQ之消息重试机制](https://juejin.im/entry/6844903809186005000)
- 明确
    > 只有当消费模式为 MessageModel.CLUSTERING(集群模式) 时，Broker才会自动进行重试，对于广播消息是不会重试的。
- 配置
    ```
    RocketMQ可在broker.conf文件中配置Consumer端的重试次数和重试时间间隔，如下：
    
    messageDelayLevel = 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
    ```
- 
- 
- 