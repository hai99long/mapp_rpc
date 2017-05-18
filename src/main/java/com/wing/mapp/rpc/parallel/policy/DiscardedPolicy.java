package com.wing.mapp.rpc.parallel.policy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by wanghl on 2017/5/3.
 */
public class DiscardedPolicy extends ThreadPoolExecutor.DiscardPolicy {
    private static final Logger LOG = LoggerFactory.getLogger(DiscardedPolicy.class);

    private String threadName;

    public DiscardedPolicy() {
        this(null);
    }

    public DiscardedPolicy(String threadName) {
        this.threadName = threadName;
    }
    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
        if (threadName != null) {
            LOG.error("RPC Thread pool [{}] is exhausted, executor={}", threadName, executor.toString());
        }
        if(!executor.isShutdown()){
            BlockingQueue queue = executor.getQueue();
            int discardSize = queue.size()>>1;
            for(int i=0; i<discardSize; i++){
                queue.poll();
            }
            queue.offer(runnable);
        }
    }
}
