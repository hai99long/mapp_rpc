package com.wing.mapp.rpc.proxy;

import com.wing.mapp.rpc.Invoker;
import com.wing.mapp.rpc.RpcInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by wanghl on 2017/4/6.
 */
public class InvokerInvocationHandler implements InvocationHandler {
    private final Invoker<?> invoker;

    public InvokerInvocationHandler(Invoker<?> handler){
        this.invoker = handler;
    }

    /**
     * 客户端的代理类调用服务端的接口
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(invoker, args);
        }
        if ("toString".equals(methodName) && parameterTypes.length == 0) {
            return invoker.toString();
        }
        if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
            return invoker.hashCode();
        }
        if ("equals".equals(methodName) && parameterTypes.length == 1) {
            return invoker.equals(args[0]);
        }
        return invoker.invoke(new RpcInvocation(method, args)).recreate();
    }
}
