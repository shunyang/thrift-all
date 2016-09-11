## 项目说明
      项目实现spring 对thrift的对象进行管理
      1 调用thrift调用时，对象注入
      2 thrift提供服务时
      
## 调用接口时配置
    <!-- thrift client 使用spring的对象管理配置
	     1 basePackage 扫描的包名
	     2 thrift config 参数
	         protocolType thrift 协议
	         	binary  TBinaryProtocol
	         	json   TJSONProtocol
	         	compact   TCompactProtocol
	         serverIp 服务ip 或者域名
	         serverPort 服务端口
	         timeOut 超时时间
	             -->
	 <bean class="com.yangyang.thrift.spring.client.ThriftClientScannerConfigurer">
            <property name="basePackage" value="com.fangdd.newhouse.baseinfo.thrift.protocol"/>
    
            <property name="thriftConfig">
                <map>
                    <entry key="protocolType">
                        <value>compact</value><!-- 客户端和服务器端保持一致，采用压缩compact protocol处理 -->
                    </entry>
                    <entry key="serverIp"><!-- 服务内部域名地址为base.nh.fdd -->
                        <value>base.nh.fdd</value>
                    </entry>
                    <entry key="serverPort"><!-- 服务端口号为30016 -->
                        <value>30016</value>
                    </entry>
                    <entry key="timeOut"><!-- 连接超时时间，设定为5秒=5000ms毫秒 -->
                        <value>5000</value>
                    </entry>
                </map>
            </property>
        </bean>
	
## 版本更新记录     
v0.1
  1 调用thrift接口时，spring对thrift对象提供注入功能      
  
v0.1.1
  1 service name 取值方式：如 TestService 类， 原来取名 testService ;现在 TestService
  
  