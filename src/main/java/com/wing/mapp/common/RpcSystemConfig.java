package com.wing.mapp.common;

/**
 * 配置类
 * Created by wanghl on 2017/5/11.
 */
public class RpcSystemConfig {
    public static final String SYSTEM_PROPERTY_THREADPOOL_QUEUE_NAME_ATTR = "com.newlandframework.rpc.parallel.queue";
    public static final long SYSTEM_PROPERTY_MESSAGE_CALLBACK_TIMEOUT = Long.getLong("nettyrpc-default-msg-timeout", 10 * 1000L);
    public static final long SYSTEM_PROPERTY_ASYNC_MESSAGE_CALLBACK_TIMEOUT = Long.getLong("nettyrpc-default-asyncmsg-timeout", 60 * 1000L);
    /**
     * 线程池线程梳理配置
     */
    public static final int SYSTEM_PROPERTY_THREADPOOL_THREAD_NUMS = Integer.getInteger("nettyrpc-default-thread-nums", 16);
    /**
     * 线程池队列容量配置
     */
    public static final int SYSTEM_PROPERTY_THREADPOOL_QUEUE_NUMS = Integer.getInteger("nettyrpc-default-queue-nums", -1);
    public static final int PARALLEL = Math.max(2, Runtime.getRuntime().availableProcessors());
    public static final String DELIMITER = ":";
}
