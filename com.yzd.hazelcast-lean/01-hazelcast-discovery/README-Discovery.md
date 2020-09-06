# discovery
## 服务发现-实现参考
- [https://docs.hazelcast.org/docs/latest/manual/html-single/index.html#kubernetes-cloud-discovery](https://docs.hazelcast.org/docs/latest/manual/html-single/index.html#kubernetes-cloud-discovery)
- [Discovering Members by TCP](Discovering Members by TCP) TPC 比较常用的方式
- [https://github.com/bitsofinfo/hazelcast-consul-discovery-spi](https://github.com/bitsofinfo/hazelcast-consul-discovery-spi)
- 可参考内部广播模式的实现：MulticastDiscoveryStrategy 

## 服务发现-续租模式-推荐
```
推荐使用：TTL模式
pass(service id)
```

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
- consul的主要接口是RESTful HTTP API
```
该API可以用来增删查改nodes、services、checks、configguration。
所有的endpoints主要分为以下类别：
kv - Key/Value存储
agent - Agent控制
catalog - 管理nodes和services
health - 管理健康监测
session - Session操作
acl - ACL创建和管理
event - 用户Events
status - Consul系统状态
#
下面我们就单独看看每个模块的具体内容。
agent
agent endpoints用来和本地agent进行交互，一般用来服务注册和检查注册，支持以下接口
/v1/agent/checks : 返回本地agent注册的所有检查(包括配置文件和HTTP接口)
/v1/agent/services : 返回本地agent注册的所有 服务
/v1/agent/members : 返回agent在集群的gossip pool中看到的成员
/v1/agent/self : 返回本地agent的配置和成员信息
/v1/agent/join/<address> : 触发本地agent加入node
/v1/agent/force-leave/<node>>: 强制删除node
/v1/agent/check/register : 在本地agent增加一个检查项，使用PUT方法传输一个json格式的数据
/v1/agent/check/deregister/<checkID> : 注销一个本地agent的检查项
/v1/agent/check/pass/<checkID> : 设置一个本地检查项的状态为passing
/v1/agent/check/warn/<checkID> : 设置一个本地检查项的状态为warning
/v1/agent/check/fail/<checkID> : 设置一个本地检查项的状态为critical
/v1/agent/service/register : 在本地agent增加一个新的服务项，使用PUT方法传输一个json格式的数据
/v1/agent/service/deregister/<serviceID> : 注销一个本地agent的服务项
#
catalog
catalog endpoints用来注册/注销nodes、services、checks
/v1/catalog/register : Registers a new node, service, or check
/v1/catalog/deregister : Deregisters a node, service, or check
/v1/catalog/datacenters : Lists known datacenters
/v1/catalog/nodes : Lists nodes in a given DC
/v1/catalog/services : Lists services in a given DC
/v1/catalog/service/<service> : Lists the nodes in a given service
/v1/catalog/node/<node> : Lists the services provided by a node
#
health
health endpoints用来查询健康状况相关信息，该功能从catalog中单独分离出来
/v1/healt/node/<node>: 返回node所定义的检查，可用参数?dc=
/v1/health/checks/<service>: 返回和服务相关联的检查，可用参数?dc=
/v1/health/service/<service>: 返回给定datacenter中给定node中service
/v1/health/state/<state>: 返回给定datacenter中指定状态的服务，state可以是"any", "unknown", "passing", "warning", or "critical"，可用参数?dc=
#
session
session endpoints用来create、update、destory、query sessions
/v1/session/create: Creates a new session
/v1/session/destroy/<session>: Destroys a given session
/v1/session/info/<session>: Queries a given session
/v1/session/node/<node>: Lists sessions belonging to a node
/v1/session/list: Lists all the active sessions
#
acl
acl endpoints用来create、update、destory、query acl
/v1/acl/create: Creates a new token with policy
/v1/acl/update: Update the policy of a token
/v1/acl/destroy/<id>: Destroys a given token
/v1/acl/info/<id>: Queries the policy of a given token
/v1/acl/clone/<id>: Creates a new token by cloning an existing token
/v1/acl/list: Lists all the active tokens
#
event
event endpoints用来fire新的events、查询已有的events
/v1/event/fire/<name>: 触发一个新的event，用户event需要name和其他可选的参数，使用PUT方法
/v1/event/list: 返回agent知道的events
#
status
status endpoints用来或者consul 集群的信息
/v1/status/leader : 返回当前集群的Raft leader
/v1/status/peers : 返回当前集群中同事
```
- [Consul 健康检查](https://www.cnblogs.com/duanxz/p/9662862.html)
```
Check必须是Script、HTTP、TCP、TTL四种类型中的一种。
Script类型需要提供Script脚本和interval变量。
HTTP类型必须提供http和Interval字段。
TCP类型需要提供tcp和Interval字段，
TTL(Timeto Live生存时间)类型秩序提供ttl。
```
- [Consul移除失效服务的正确姿势](https://blog.csdn.net/weixin_34244102/article/details/86236068)
```
解决办法：调用deregister接口
```

## 定时线程池中scheduleWithFixedDelay和scheduleAtFixedRate的区别
- [定时线程池中scheduleWithFixedDelay和scheduleAtFixedRate的区别](https://blog.csdn.net/weixin_35756522/article/details/81707276)

