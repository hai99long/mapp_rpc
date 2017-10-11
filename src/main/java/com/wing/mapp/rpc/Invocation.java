package com.wing.mapp.rpc;

/**
 * Created by wanghl on 2017/4/5.
 */
public interface Invocation {
    /**
     * get method name.
     *
     * @serial
     * @return method name.
     */
    String getMethodName();

    String getClassName();

    /**
     * get parameter types.
     *
     * @serial
     * @return parameter types.
     */
    Class<?>[] getParameterTypes();

    /**
     * get arguments.
     *
     * @serial
     * @return arguments.
     */
    Object[] getArguments();

    /**
     * get the invoker in current context.
     *
     * @transient
     * @return invoker.
     */
    Invoker<?> getInvoker();

}
