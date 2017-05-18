package com.wing.mapp.rpc.protocol;

import com.wing.mapp.rpc.Invocation;
import com.wing.mapp.rpc.Invoker;
import com.wing.mapp.rpc.Result;
import com.wing.mapp.rpc.RpcResult;

/**
 * Created by wanghl on 2017/4/6.
 */
public abstract class WingServerInvoker<T> implements Invoker<T> {
    private final T proxy;


    public WingServerInvoker(T proxy){
        if (proxy == null) {
            throw new IllegalArgumentException("proxy == null");
        }
        this.proxy = proxy;
    }
    public Class<T> getInterface() {
        return null;
    }

    public Result invoke(Invocation invocation) throws Throwable {
        Result result =  new RpcResult(doInvoke(proxy, invocation.getMethodName(), invocation.getParameterTypes(), invocation.getArguments()));
        return result;
    }
    protected abstract Object doInvoke(T proxy, String methodName, Class<?>[] parameterTypes, Object[] arguments) throws Throwable;
}
