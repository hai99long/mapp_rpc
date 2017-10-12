# mapp_rpc
**mapp项目是一个利用netty实现的rpc框架，其用到的技术主要有**：<br>
* 利用netty实现数据交换，netty网络模型采用主从Reactor线程模型
* 服务端采用guava线程池对请求进行异步回调处理
* 消息网络传输的序列化方式目前可以采用kyro、protostuff
* rpc服务的启动、注册、卸载可以利用spring标签统一管理
* 采用javassist生成服务代理类、包装类
* 采用testng进行单元测试
## English Introduction
**The mapp project is the use of a netty implementation of the RPC framework, the use of technology are mainly**: <br>
* The data exchange is realized by using netty, and the netty network model adopts the master-slave Reactor thread model
* The server uses the guava thread pool to process the request For asynchronous callback handler
* The serialization of message network transmission can now take kyro and protostuff
* The startup, registration, and uninstall of RPC services can be managed uniformly using the spring tag
* Using javassist to generate service proxy classes and wrapper classes
* Unit testing using TestNG

# 快速开始
1、开发服务端的服务类及实现类，并如下面这样配置在spring的配置文件中：<br>
```Java
\<bean id="com.wing.service.RpcTestService" class="com.wing.service.impl.RpcTestServiceImpl"/> 
```
2、在客户端的spring配置文件中，配置如下代码：<br>
```Java
\<nettyrpc:reference id="rpcTestService" interfaceName="com.wing.service.RpcTestService"
                        protocol="kyro" address="127.0.0.1:28880" serviceId="rpcTestService"/> 
```
3、在服务端调用NettyServer的start方法则可以启动服务端
4、在客户端就跟普通的调用spring的bean一样调用即可，例子可以看测试类TestRpc中的方法，如：<br>
```Java
   RpcTestService rpcTestService = (RpcTestService)applicationContext.getBean("rpcTestService"); 
   String result = rpcTestService.strConcatUUID("wanghl");
```
 ## English Introduction
1、Develop the service classes and implementation classes of the server, and configure them in the spring configuration file as follows：<br>
```Java
\<bean id="com.wing.service.RpcTestService" class="com.wing.service.impl.RpcTestServiceImpl"/>
```
2、In the client's spring configuration file, configure the following code：<br>
```Java
\<nettyrpc:reference id="rpcTestService" interfaceName="com.wing.service.RpcTestService"
                        protocol="kyro" address="127.0.0.1:28880" serviceId="rpcTestService"/>
```                        
3、When invoking the NettyServer's start method on the server side, you can start the server
4、In the client, just like the ordinary call spring bean, the example can be seen in the test class TestRpc methods, such as：<br>
```Java
RpcTestService rpcTestService = (RpcTestService)applicationContext.getBean("rpcTestService"); <br>
String result = rpcTestService.strConcatUUID("wanghl");
```
