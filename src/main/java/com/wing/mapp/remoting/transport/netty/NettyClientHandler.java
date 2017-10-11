package com.wing.mapp.remoting.transport.netty;

import com.wing.mapp.remoting.exchange.Request;
import com.wing.mapp.remoting.exchange.Response;
import com.wing.mapp.remoting.exchange.ResultFuture;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wanghl on 2017/4/8.
 * 客户端处理
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<Response> {
    private ConcurrentHashMap<Long, ResultFuture> mapResultFuture = new ConcurrentHashMap<Long, ResultFuture>();
    private volatile Channel channel;
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Response response) throws Exception {
        //System.out.println(((RpcResult)response.getResult()).getValue()+"|"+response.getId());
        long requestId = response.getId();
        ResultFuture resultFuture = mapResultFuture.get(requestId);
        if(resultFuture!=null){
            mapResultFuture.remove(requestId);
            resultFuture.receive(response);
        }
    }
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
    public ResultFuture sendRequest(Request request){
        ResultFuture resultFuture = new ResultFuture(request);
        mapResultFuture.put(request.getId(),resultFuture);
        channel.writeAndFlush(request);  //发送请求至服务端
        return resultFuture;
    }
}
