package com.wing.mapp.remoting.exchange;

import com.wing.mapp.rpc.Result;
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
    public RpcResult get() throws InterruptedException{
        try{
            lock.lock();
            finish.await(1000, TimeUnit.MILLISECONDS);
            if(this.response.getResult()!=null)
                return (RpcResult) this.response.getResult();
            else
                return null;
        }finally {
            lock.unlock();
        }
    }
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
