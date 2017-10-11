package com.wing.mapp.remoting.transport.netty;

import com.wing.mapp.remoting.exchange.Request;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by wanghl on 2017/4/8.
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<Request> {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:rpc-invoke-config-server.xml");
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Request request) throws Exception {
        NettyServerInitializeTask recvTask = new NettyServerInitializeTask(context,request);
        NettyServer.submit(recvTask,channelHandlerContext,request);
    }
}
