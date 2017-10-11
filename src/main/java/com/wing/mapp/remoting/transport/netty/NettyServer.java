package com.wing.mapp.remoting.transport.netty;

import com.google.common.util.concurrent.*;
import com.wing.mapp.common.codec.MessageCodecUtil;
import com.wing.mapp.common.codec.MessageDecoder;
import com.wing.mapp.common.codec.MessageEncoder;
import com.wing.mapp.common.codec.kyro.KryoCodecUtil;
import com.wing.mapp.common.codec.kyro.KryoPoolFactory;
import com.wing.mapp.remoting.exchange.Request;
import com.wing.mapp.remoting.exchange.Response;
import com.wing.mapp.rpc.parallel.NamedThreadFactory;
import com.wing.mapp.rpc.parallel.RpcThreadPool;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by wanghl on 2017/4/25.
 */
public class NettyServer  {
    private static volatile ListeningExecutorService threadPoolExecutor;
    private static int threadNums = 16;
    private static int queueNums = -1;
    ThreadFactory threadRpcFactory = new NamedThreadFactory("NettyServer ThreadFactory");
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup(Math.max(2, Runtime.getRuntime().availableProcessors())*2, threadRpcFactory);
    private MessageCodecUtil util = new KryoCodecUtil(KryoPoolFactory.getKryoPoolInstance());
    public void start(){
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new MessageDecoder(util));
                            channel.pipeline().addLast(new MessageEncoder(util));
                            channel.pipeline().addLast("handler",new NettyServerHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    //Boss线程内存池配置.
                    .option(ChannelOption.ALLOCATOR,PooledByteBufAllocator.DEFAULT)
                    //Work线程内存池配置.
                    .childOption(ChannelOption.ALLOCATOR,PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.bind("127.0.0.1", 28880).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
    public void stop(){
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
    public static void submit(Callable<Response> task, final ChannelHandlerContext ctx, final Request request) {
        if (threadPoolExecutor == null) {
            synchronized (NettyServer.class) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = MoreExecutors.listeningDecorator((ThreadPoolExecutor)  RpcThreadPool.getExecutor(threadNums, queueNums));
                }
            }
        }

        ListenableFuture<Response> listenableFuture = threadPoolExecutor.submit(task);
        Futures.addCallback(listenableFuture, new FutureCallback<Response>() {
            public void onSuccess(final Response response) {
                ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        System.out.println("RPC Server Send message-id respone:" + response.getResult());
                    }
                });
            }

            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, threadPoolExecutor);
    }
    public static void main(String[] args){
        NettyServer nettyServer = new NettyServer();
        nettyServer.start();
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
