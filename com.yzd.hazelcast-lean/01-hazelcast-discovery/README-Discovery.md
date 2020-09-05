# discovery
## 服务发现-实现参考
- [https://docs.hazelcast.org/docs/latest/manual/html-single/index.html#kubernetes-cloud-discovery](https://docs.hazelcast.org/docs/latest/manual/html-single/index.html#kubernetes-cloud-discovery)
- [Discovering Members by TCP](Discovering Members by TCP) TPC 比较常用的方式
- [https://github.com/bitsofinfo/hazelcast-consul-discovery-spi](https://github.com/bitsofinfo/hazelcast-consul-discovery-spi)
- 可参考内部广播模式的实现：MulticastDiscoveryStrategy 

## etcd-discovery-delay-ms 解释：
```
 <!--  
    If you quickly start an entire hz cluster at the exact same time, and
    all hazelcast nodes are registered with Etcd at the same time, its 
    possible they may all discover ONLY themselves as members, leading to a cluster
    that can never be fully discovered. This small delay can assist with avoiding 
    that problem. @see https://github.com/hazelcast/hazelcast/issues/6813 
-->
```


## consul
- [Consul-启动参考](https://www.jianshu.com/p/ce7c7b9dcf14)
- 启动consul
````
1.
查看版本
./consul -v
2.
开发模式启动单节点
启动命令，添加红色部分才能被外网访问，并有web页面
./consul agent -dev -ui -client 0.0.0.0 
打开管理页面
http://127.0.0.1:8500/
查看consul节点
curl http://127.0.0.1:8500/v1/catalog/nodes

````