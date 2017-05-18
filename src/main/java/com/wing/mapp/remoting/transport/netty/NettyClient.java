package com.wing.mapp.remoting.transport.netty;

import com.google.common.util.concurrent.*;
import com.wing.mapp.common.RpcSystemConfig;
import com.wing.mapp.rpc.parallel.RpcThreadPool;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by wanghl on 2017/5/9.
 */
public class NettyClient {
    private static volatile NettyClient rpcClientLoader;
    private NettyClientHandler messageSendHandler = null;
    private static final int parallel = RpcSystemConfig.PARALLEL * 2;
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(parallel);
    private static int threadNums = RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_THREAD_NUMS;
    private static int queueNums = RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_QUEUE_NUMS;
    private ListeningExecutorService threadPoolExecutor = MoreExecutors.listeningDecorator((ThreadPoolExecutor) RpcThreadPool.getExecutor(threadNums,queueNums));
    private Lock lock = new ReentrantLock();
    private Condition connectStatus = lock.newCondition();
    private Condition handlerStatus = lock.newCondition();
    private NettyClient() {
    }

    public static NettyClient getInstance() {
        if (rpcClientLoader == null) {
            synchronized (NettyClient.class) {
                if (rpcClientLoader == null) {
                    rpcClientLoader = new NettyClient();
                }
            }
        }
        return rpcClientLoader;
    }
    public void load(String serverAddress){
        String[] address = serverAddress.split(RpcSystemConfig.DELIMITER);
        if (address.length == 2) {
            final InetSocketAddress remoteAddr = new InetSocketAddress(address[0], Integer.parseInt(address[1]));
            ListenableFuture<Boolean> listenableFuture = threadPoolExecutor.submit(new NettyClientInitializeTask(eventLoopGroup,remoteAddr));
            Futures.addCallback(listenableFuture, new FutureCallback<Boolean>() {
                public void onSuccess(Boolean result) {
                    try{
                        lock.lock();
                        if(messageSendHandler==null)
                            handlerStatus.await();
                        if((result==Boolean.TRUE) && messageSendHandler!=null)
                            connectStatus.signalAll();
                    }catch (InterruptedException e){
                        Logger.getLogger(NettyClient.class.getName()).log(Level.SEVERE, null, e);
                    }finally {
                        lock.unlock();
                    }
                }

                public void onFailure(Throwable throwable) {
                    throwable.printStackTrace();
                }
            },threadPoolExecutor);
        }
    }
    public void setMessageSendHandler(NettyClientHandler messageInHandler) {
        try {
            lock.lock();
            this.messageSendHandler = messageInHandler;
            handlerStatus.signal();
        } finally {
            lock.unlock();
        }
    }

    public NettyClientHandler getMessageSendHandler() throws InterruptedException {
        try {
            lock.lock();
            if (messageSendHandler == null) {
                connectStatus.await();
            }
            return messageSendHandler;
        } finally {
            lock.unlock();
        }
    }
}
