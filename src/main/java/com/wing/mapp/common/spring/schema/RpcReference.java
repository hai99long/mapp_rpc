package com.wing.mapp.common.spring.schema;

import com.google.common.eventbus.EventBus;
import com.wing.mapp.event.ClientStopEvent;
import com.wing.mapp.event.ClientStopEventListener;
import com.wing.mapp.remoting.transport.netty.NettyClientExecutor;
import com.wing.mapp.rpc.protocol.WingClientInvoker;
import com.wing.mapp.rpc.proxy.InvokerInvocationHandler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by wanghl on 2017/6/3.
 * reference标签实现类
 */
public class RpcReference implements FactoryBean,InitializingBean,DisposableBean {
    private String interfaceName;
    private String address;
    private String protocol;
    private String serviceId;
    private EventBus eventBus = new EventBus(); //用于在销毁时同时关闭netty服务、线程池服务

    public void destroy() throws Exception {
        eventBus.post(new ClientStopEvent(0));
    }

    /**
     * 客户端获取接口的实现类
     * reference标签中的interfaceName接口的实现类是用javassist实现的代理类
     * @return
     * @throws Exception
     */
    public Object getObject() throws Exception {
        return NettyClientExecutor.getInstance().execute(getObjectType()).newInstance(new InvokerInvocationHandler(new WingClientInvoker()));
    }

    /**
     * 获取接口类型
     * @return
     */
    public Class<?> getObjectType() {
        try {
            return this.getClass().getClassLoader().loadClass(interfaceName);
        } catch (ClassNotFoundException e) {
            System.err.println("spring analyze fail!");
        }
        return null;
    }

    public boolean isSingleton() {
        return true;
    }

    public void afterPropertiesSet() throws Exception {
        NettyClientExecutor.getInstance().setRpcServerLoader(address, protocol);
        ClientStopEventListener listener = new ClientStopEventListener();
        eventBus.register(listener);
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
