package com.wing.mapp.rpc;

/**
 * Created by wanghl on 2017/3/31.
 */
public interface Invoker<T> {
    /**
     * get service interface.
     *
     * @return service interface.
     */
    Class<T> getInterface();

    /**
     * invoke.
     *
     * @param invocation
     * @return result
     */
    Result invoke(Invocation invocation) throws Throwable;
}
