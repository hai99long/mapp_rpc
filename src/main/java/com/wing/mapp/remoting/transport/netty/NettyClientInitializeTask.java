package com.wing.mapp.remoting.transport.netty;

import com.wing.mapp.common.codec.kyro.KryoCodecUtil;
import com.wing.mapp.common.codec.kyro.KryoPoolFactory;
import com.wing.mapp.remoting.transport.netty.codec.NettyKryoDecoder;
import com.wing.mapp.remoting.transport.netty.codec.NettyKryoEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;

/**
 * Created by wanghl on 2017/5/6.
 */
public class NettyClientInitializeTask implements Callable<Boolean> {
    private EventLoopGroup eventLoopGroup = null;
    private InetSocketAddress serverAddress = null;
    private KryoCodecUtil util = new KryoCodecUtil(KryoPoolFactory.getKryoPoolInstance());
    public NettyClientInitializeTask(EventLoopGroup eventLoopGroup,InetSocketAddress serverAddress){
        this.eventLoopGroup = eventLoopGroup;
        this.serverAddress = serverAddress;
    }

    public Boolean call() throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch)throws Exception {
                        ch.pipeline().addLast(new NettyKryoDecoder(util));
                        ch.pipeline().addLast(new NettyKryoEncoder(util));
                        ch.pipeline().addLast("handler",new NettyClientHandler());
                    }
                }).option(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture channelFuture = bootstrap.connect(serverAddress);
        channelFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                NettyClientHandler handler = channelFuture.channel().pipeline().get(NettyClientHandler.class);
                NettyClient.getInstance().setMessageSendHandler(handler);
            }
        });
        return Boolean.TRUE;
    }
}
