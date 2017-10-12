# mapp_rpc
**mapp项目是一个利用netty实现的rpc框架，其用到的技术主要有**：<br>
* 利用netty实现数据交换，netty网络模型采用主从Reactor线程模型
* 服务端采用guava线程池对请求进行异步回调处理
* 消息网络传输的序列化方式目前可以采用kyro、protostuff
* rpc服务的启动、注册、卸载可以利用spring标签统一管理
* 采用javassist生成服务代理类、包装类
* 采用testng进行单元测试
# 快速开始
1、开发服务端的服务类及实现类，并如下面这样配置在spring的配置文件中：<br>
\<bean id="com.wing.service.RpcTestService" class="com.wing.service.impl.RpcTestServiceImpl"/> <br>
2、在客户端的spring配置文件中，配置如下代码：<br>
\<nettyrpc:reference id="rpcTestService" interfaceName="com.wing.service.RpcTestService"
                        protocol="kyro" address="127.0.0.1:28880" serviceId="rpcTestService"/> <br>
3、在服务端调用NettyServer的start方法则可以启动服务端
4、在客户端就跟普通的调用spring的bean一样调用即可，例子可以看测试类TestRpc中的方法，如：<br>
   RpcTestService rpcTestService = (RpcTestService)applicationContext.getBean("rpcTestService"); <br>
   String result = rpcTestService.strConcatUUID("wanghl");
