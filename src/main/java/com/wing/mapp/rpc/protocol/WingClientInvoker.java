package com.wing.mapp.rpc.protocol;

import com.wing.mapp.remoting.exchange.Request;
import com.wing.mapp.remoting.exchange.ResultFuture;
import com.wing.mapp.remoting.transport.netty.NettyClient;
import com.wing.mapp.remoting.transport.netty.NettyClientHandler;
import com.wing.mapp.rpc.Invocation;
import com.wing.mapp.rpc.Invoker;
import com.wing.mapp.rpc.Result;

/**
 * Created by wanghl on 2017/3/31.
 * 客户端请求的invoker，用于包装客户端的请求
 */
public class WingClientInvoker implements Invoker<Request> {
    public Class<Request> getInterface() {
        return Request.class;
    }

    public WingClientInvoker(){
    }

    /**
     * 请求服务端
     * @param invocation
     * @return
     */
    public Result invoke(Invocation invocation) {
        Request request = new Request();
        request.setData(invocation);
        NettyClientHandler handler = null;
        try {
            handler = NettyClient.getInstance().getMessageSendHandler();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ResultFuture resultFuture = handler.sendRequest(request);  //发送请求至服务端
        try {
            return resultFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
