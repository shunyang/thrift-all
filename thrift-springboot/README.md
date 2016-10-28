#spring-boot-thrift-server

#### 2016-05-09 更新

1. 改变实现机制,项目启动后 使用spring ApplicationContextEvent 扫描带有 @EnableThriftServer 的注解

2. 使用 TMultiplexedProcessor 以支持多接口thrift服务



### 开发步骤

#### 接口定义 
 
1. 定义 xxx.thrift 文件
2. 使用thrift 命令生成 java 文件
3. 拷贝java 文件到项目src下


### 接口实现

1. 集成 xxx.Iace 接口 和 ThriftServerService
2. 使用 @EnableThriftServer 注解


### 集成

       new SpringApplicationBuilder().listeners(new ThriftApplicationListener()).sources(App.class).run(args);

