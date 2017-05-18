package com.wing.mapp.rpc.parallel;

import com.wing.mapp.rpc.parallel.policy.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Created by wanghl on 2017/5/3.
 */
public class RpcThreadPool {
    private static final Logger LOG = LoggerFactory.getLogger(RpcThreadPool.class);
    private static RejectedExecutionHandler createPolicy(){
        RejectedPolicyType rejectedPolicyType = RejectedPolicyType.getRejectedPolicyType("AbortPolicy");
        switch (rejectedPolicyType) {
            case ABORT_POLICY:
                return new AbortPolicy();
            case BLOCKING_POLICY:
                return new BlockingPolicy();
            case CALLER_RUNS_POLICY:
                return new CallerRunsPolicy();
            case DISCARDED_POLICY:
                return new DiscardedPolicy();
            case DISCARDED_OLDEST_POLICY:
                return new DiscardOldestPolicy();
        }
        return null;
    }
    private static BlockingQueue<Runnable> createBlockingQueue(int capacity){
        BlockingQueueType queueType = BlockingQueueType.getBlockingQueue("LinkedBlockingQueue");
        switch (queueType){
            case LINKED_BLOCKING_QUEUE:
                return new LinkedBlockingQueue<Runnable>();
            case ARRAY_BLOCKING_QUEUE:
                return new ArrayBlockingQueue<Runnable>(Math.max(2, Runtime.getRuntime().availableProcessors())*capacity);
            case SYNCHRONOUS_QUEUE:
                return new SynchronousQueue<Runnable>();
        }
        return null;
    }
    public static Executor getExecutor(int poolSize, int capacity){
        LOG.info("ThreadPool Core[poolSize:" + poolSize + ", capacity:" + capacity + "]");
        String name = "RpcThreadPool";
        ThreadPoolExecutor executor = new ThreadPoolExecutor(poolSize,poolSize,0,TimeUnit.MILLISECONDS,
                createBlockingQueue(capacity), new NamedThreadFactory(name, true),createPolicy());
        return executor;
    }
}
