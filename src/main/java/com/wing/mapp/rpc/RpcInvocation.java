package com.wing.mapp.rpc;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wanghl on 2017/4/6.
 */
public class RpcInvocation implements Invocation {
    private static final long serialVersionUID = -4355285085441097045L;

    private String methodName;

    private String className;

    private Class<?>[]           parameterTypes;

    private Object[]             arguments;

    private transient Invoker<?> invoker;

    public RpcInvocation() {
    }


    public RpcInvocation(Method method, Object[] arguments) {
        this(method.getDeclaringClass().getName(),method.getName(), method.getParameterTypes(),arguments);
    }


    public RpcInvocation(String className,String methodName, Class<?>[] parameterTypes, Object[] arguments) {
        this(className,methodName, parameterTypes, arguments, null);
    }

    public RpcInvocation(String className,String methodName, Class<?>[] parameterTypes, Object[] arguments, Invoker<?> invoker) {
        this.className = className;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes == null ? new Class<?>[0] : parameterTypes;
        this.arguments = arguments == null ? new Object[0] : arguments;
        this.invoker = invoker;
    }

    public Invoker<?> getInvoker() {
        return invoker;
    }

    public void setInvoker(Invoker<?> invoker) {
        this.invoker = invoker;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public Object[] getArguments() {
        return arguments;
    }


    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes == null ? new Class<?>[0] : parameterTypes;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments == null ? new Object[0] : arguments;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return "RpcInvocation [methodName=" + methodName + ", parameterTypes="
                + Arrays.toString(parameterTypes) + ", arguments=" + Arrays.toString(arguments)
                + "]";
    }
}
