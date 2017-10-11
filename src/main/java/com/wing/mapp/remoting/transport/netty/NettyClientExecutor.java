package com.wing.mapp.remoting.transport.netty;

import com.wing.mapp.common.bytecode.Proxy;

/**
 * Created by wanghl on 2017/6/3.
 */
public class NettyClientExecutor {
    private static class MessageSendExecutorHolder {
        private static final NettyClientExecutor instance = new NettyClientExecutor();
    }
    public static NettyClientExecutor getInstance() {
        return MessageSendExecutorHolder.instance;
    }
    private NettyClient nettyClient = NettyClient.getInstance();
    public static Proxy execute(Class rpcInterface) {
        return  Proxy.getProxy(rpcInterface);
    }
    public void stop() {
        nettyClient.unLoad();
    }
    public void setRpcServerLoader(String serverAddress,String protocol) {
        nettyClient.load(serverAddress, protocol);
    }
}
