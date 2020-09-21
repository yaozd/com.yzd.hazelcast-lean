# management-center

## 官方文档
- [https://docs.hazelcast.org/docs/latest/manual/html-single/index.html#set](https://docs.hazelcast.org/docs/latest/manual/html-single/index.html#set)
- []()

## 配置参考
- [Hazelcast.xml 配置文件说明](https://my.oschina.net/vdroid/blog/754883)
- [Hazelcast配置文档（完整）](https://my.oschina.net/vdroid/blog/754882)
- [Hazelcast数据分发和集群平台管理中心的下载以及和SpringBoot进行集成](https://www.jianshu.com/p/f32a24771d17)
- []()

## 测试地址
```
http://localhost:8081/getValue
http://localhost:8081/setValue
```

## 内存占用
```
1.SET (不分区)
1000K的UUID 内存：100MB     
2.MAP（分区）
1000K的UUID 内存：200MB
3.ReplicatedMap （不支持分区的Map数据结构，集群所有成员都有全量数据）
1000K的UUID 内存：200MB
ps: .\hey_windows_amd64.exe -n 1000000 -c 100 http://localhost:8081/setValue
总结：
hazelcast 维持连接关系推荐使用set数据结构
```

## 版本号：
```
1.Management center
https://download.hazelcast.com/management-center/management-center-3.8.3.zip
PATH:百度云=》个人项目-开源=》H-Hazelcast=》M-hazelcast-management-center=》management-center-3.8.3.zip
2.hazelcast
<dependency>
  <groupId>com.hazelcast</groupId>
  <artifactId>hazelcast</artifactId>
  <version>3.8.3</version>
</dependency>
PS:管理中心与hazelcast的版本号必须一致
```
## grpc-数据推送
```
自动组网:grpc-port
```
## 账号：
```
http://localhost:8080/mancenter
admin
YZD1qaz2wsx
```
## 参考：
- [SpringBoot集成Hazelcast实现集群与分布式内存缓存](https://zhuanlan.zhihu.com/p/51260151)
- [Hazelcast数据分发和集群平台管理中心的下载以及和SpringBoot进行集成](https://www.jianshu.com/p/f32a24771d17)
- []()

## 测试数据生成
- [hey接口压测工具](https://www.jianshu.com/p/43bd10bb925a)
```
.\hey_windows_amd64.exe -n 10000000 -c 10 http://localhost:8081/setValue
.\hey_windows_amd64.exe -z 120s -c 100 http://localhost:8081/setValue
//
简单实用
get请求，指定2s时间内，并发为1000
hey -z 2s -c 1000 http://www.httpbin.org

get请求，运行1000次，指定并发为100
hey -n 1000 -c 100 http://www.httpbin.org

post请求
hey -z 2s -c 100 -m POST -H "Content-Type: application/json" -d "{'name': '张三'}" http://www.httpbin.org
```