package com.wing.mapp.remoting.exchange;

import com.wing.mapp.rpc.RpcResult;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by wanghl on 2017/5/8.
 */
public class ResultFuture {
    private Request request;
    private Response response;
    private Lock lock = new ReentrantLock();
    private Condition finish = lock.newCondition();

    public ResultFuture(Request request){
        this.request = request;
    }

    /**
     * 获取服务端的响应结果
     * @return
     * @throws InterruptedException
     */
    public RpcResult get() throws InterruptedException{
        try{
            lock.lock();   //锁一下
            finish.await(1000, TimeUnit.MILLISECONDS);  //receive方法会发信号，以使finish放弃等待
            if(this.response.getResult()!=null)
                return (RpcResult) this.response.getResult();
            else
                return null;
        }finally {
            lock.unlock();
        }
    }

    /**
     * 收到服务端的响应信息，发送信号给finish，让其放弃等待
     * @param response
     */
    public void receive(Response response){
        try{
            lock.lock();
            finish.signal();
            this.response = response;
        }finally {
            lock.unlock();
        }
    }
}
