# discover v2

## 官方文档
- [https://docs.hazelcast.org/docs/latest/manual/html-single/index.html#set](https://docs.hazelcast.org/docs/latest/manual/html-single/index.html#set)
- [Hazelcast: Threading Model](https://docs.hazelcast.org/docs/latest/manual/html-single/index.html#threading-model)
```
1.
I/O Threading
hazelcast.io.input.thread.count and hazelcast.io.output.thread.count
2.
Event Threading
hazelcast.event.thread.count
3.
Operation Threading
hazelcast.operation.thread.count
4.
IExecutor Threading
```

## 配置参考
- [Hazelcast.xml 配置文件说明](https://my.oschina.net/vdroid/blog/754883)
- [Hazelcast配置文档（完整）](https://my.oschina.net/vdroid/blog/754882)
- [Hazelcast数据分发和集群平台管理中心的下载以及和SpringBoot进行集成](https://www.jianshu.com/p/f32a24771d17)
- []()

## 参考：
- [java之比较两个日期大小](https://blog.csdn.net/dongfangbaiyun/article/details/51225469)
```
if(startDate.before(endDate)){
    System.out.println("startDate小于endDate");
}else if(startDate.after(endDate)){
    System.out.println("startDate大于endDate");
}else{
    System.out.println("startDate等于endDate");
}
```