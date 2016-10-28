## thrift的各种服务
 本工程为thrift提供的各种服务

### 1.thrift-pool工程

thrift连接池 


### 2.thrift-service工程

基于thrift的微服务框架
thrift不仅支持tcp/ip协议的rpc调用，也支持http协议的rest服务调用，同一个项目中甚至可同时支持这二种方式

#### 2.1支持rpc调用
支持常规的tcp/ip协议的rpc调用

#### 2.2 支持http协议的servlet调用 
1、 支持以servlet方式嵌入web容器(tomcat/weblogic/jboss之类)运行
2、 也可以直接用嵌入式jetty直接从jar包运行

#### 2.3支持javascript调用
支持js直接调用,post的json格式为:
以下格式无需手动拼写，thrift生成的js客户端会自动封装及解析
[1,"hello",1,0,{"1":{"str":"jimmy"}}]
返回结果以json格式返回:
[1,"hello",2,0,{"0":{"str":"hello,jimmy"}}]
#### 2.4部署方式
本框架支持以下二种部署方式：
1、 jetty嵌入式模式,mvn package将在target目录下生成可直接运行的thrift-service.jar, 然后java -jar thrift-service.jar
2、 将pom.xml中的<packaging>jar</packaging>中的jar改成war,同时注释掉plug中的maven-shade-plugin，然后mvn package 生成war包,可部署到任何兼容servlet 2.5+的web容器
注：方式1下，默认http端口为8080，如需修改，可在启动时指定端口，例如：java -jar thrift-service.jar -port=9090 , rpc端口在src/main/resources/spring-thrift.xml中修改

#### 2.5测试运行
1、 js调用直接浏览http://localhost:8080/thrift-service/ ,点击页面的call thrift按钮,即可测试js方式直接调用
2、 src/test/java/com/yangyang/thrift/proxy/ThriftServiceClientTest.java 里提供了rpc及http方式调用的测试用例

### 3.thrift-springboot工程

通过spring boot启动的时候注册thrift服务的方式，提供对外的thrift服务

### 4.thrift-zookeeper-rpc工程

通过zookeeper将thrift服务提供者与消费者联系起来，并提供spring配置的方式引入与消费服务

