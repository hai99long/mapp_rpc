package com.wing.mapp.rpc.protocol;

import com.wing.mapp.remoting.exchange.Request;
import com.wing.mapp.remoting.exchange.ResultFuture;
import com.wing.mapp.remoting.transport.netty.NettyClient;
import com.wing.mapp.remoting.transport.netty.NettyClientHandler;
import com.wing.mapp.rpc.Invocation;
import com.wing.mapp.rpc.Invoker;
import com.wing.mapp.rpc.Result;
import com.wing.mapp.rpc.RpcResult;

/**
 * Created by wanghl on 2017/3/31.
 */
public class WingClientInvoker<T> implements Invoker<Request> {
    private Request request;
    public Class<Request> getInterface() {
        return Request.class;
    }

    public WingClientInvoker(Request request){
        this.request = request;
    }
    public Result invoke(Invocation invocation) {
        request.setData(invocation);
        NettyClientHandler handler = null;
        try {
            handler = NettyClient.getInstance().getMessageSendHandler();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ResultFuture resultFuture = handler.sendRequest(request);
        try {
            return resultFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
