本文参考https://github.com/slimina/thrift-zookeeper-rpc 改造

## 1.zookeeper中的数据存储设计
作为具体业务服务的RPC服务发布方, 对其自身的服务描述由以下元素构成.
　　1). namespace: 命名空间，来区分不同应用 
　　2). service: 服务接口, 采用发布方的类全名来表示
　　3). version: 版本号
　　借鉴了Maven的GAV坐标系, 三维坐标系更符合服务平台化的大环境. 
　　*) 数据模型的设计
　　具体RPC服务的注册路径为: /rpc/{namespace}/{service}/{version}, 该路径上的节点都是永久节点
　　RPC服务集群节点的注册路径为: /rpc/{namespace}/{service}/{version}/{ip:port:weight}, 末尾的节点是临时节点.

##2 .对于Thrift服务化的改造，主要是客户端，可以从如下几个方面进行：

1.服务端的服务注册，客户端自动发现，无需手工修改配置，这里我们使用zookeeper，但由于zookeeper本身提供的客户端使用较为复杂，
   因此采用curator-recipes工具类进行处理服务的注册与发现。

2.客户端使用连接池对服务调用进行管理，提升性能，这里我们使用Apache Commons项目commons-pool，可以大大减少代码的复杂度。

3.关于Failover/LoadBalance，由于zookeeper的watcher，当服务端不可用是及时通知客户端，并移除不可用的服务节点，而LoadBalance
  有很多算法，这里我们采用随机加权方式，也是常有的负载算法，至于其他的算法介绍参考:常见的负载均衡的基本算法。

4.使thrift服务的注册和发现可以基于spring配置,可以提供很多的便利。

5.其他的改造如：

1）通过动态代理实现client和server端的交互细节透明化，让用户只需通过服务方提供的接口进行访问

2）Thrift通过两种方式调用服务Client和Iface

// Service 接口 调用

(EchoService.Iface)service.echo("hello lilei"); 
