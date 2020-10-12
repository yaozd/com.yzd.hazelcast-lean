

## 参考
- [Spring Boot整合Rocketmq](https://blog.csdn.net/qq_18603599/article/details/81172866)
- [message数据结构](https://blog.csdn.net/qq_32711825/article/details/78579864)
  ```
  keys :代表这条消息的业务关键词，服务器会根据keys创建哈希索引，
  设置后，可以在console系统根据topic keys来查询消息，由于是哈希索引，
  尽可能保证Key唯一，例如订单号，商品ID等
  ```