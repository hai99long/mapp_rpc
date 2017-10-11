package com.wing.mapp.remoting.transport.netty;

import com.wing.mapp.common.codec.MessageCodecUtil;
import com.wing.mapp.common.codec.MessageDecoder;
import com.wing.mapp.common.codec.MessageEncoder;
import com.wing.mapp.common.codec.kyro.KryoCodecUtil;
import com.wing.mapp.common.codec.kyro.KryoPoolFactory;
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
    private String protocol = null;
    private MessageCodecUtil util = new KryoCodecUtil(KryoPoolFactory.getKryoPoolInstance());
    public NettyClientInitializeTask(EventLoopGroup eventLoopGroup,InetSocketAddress serverAddress,String protocol){
        this.eventLoopGroup = eventLoopGroup;
        this.serverAddress = serverAddress;
        this.protocol = protocol;
    }

    /**
     * 线程池中调用，建立到服务端的链接
     * @return
     * @throws Exception
     */
    public Boolean call() throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch)throws Exception {
                        ch.pipeline().addLast(new MessageDecoder(util));
                        ch.pipeline().addLast(new MessageEncoder(util));
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
