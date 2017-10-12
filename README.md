# mapp_rpc
**mapp项目是一个利用netty实现的rpc框架，其用到的技术主要有**：<br>
* 利用netty实现数据交换，netty网络模型采用主从Reactor线程模型
* 服务端采用guava线程池对请求进行异步回调处理
* 消息网络传输的序列化方式目前可以采用kyro、protostuff
* rpc服务的启动、注册、卸载可以利用spring标签统一管理
* 采用javassist生成服务代理类、包装类
# 开发指南
