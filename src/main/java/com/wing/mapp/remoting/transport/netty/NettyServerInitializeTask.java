package com.wing.mapp.remoting.transport.netty;

import com.wing.mapp.common.bytecode.Wrapper;
import com.wing.mapp.remoting.exchange.Request;
import com.wing.mapp.remoting.exchange.Response;
import com.wing.mapp.rpc.Invoker;
import com.wing.mapp.rpc.Result;
import com.wing.mapp.rpc.RpcInvocation;
import com.wing.mapp.rpc.protocol.WingServerInvoker;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.Callable;

/**
 * Created by wanghl on 2017/5/6.
 */
public class NettyServerInitializeTask implements Callable<Response> {
    private Request request;
    private ClassPathXmlApplicationContext context;
    public NettyServerInitializeTask(ClassPathXmlApplicationContext context,Request request){
        this.context = context;
        this.request = request;
    }

    /**
     * 响应客户端的请求
     * @return
     * @throws Exception
     */
    public Response call() throws Exception {
        Response response = new Response(request.getId());
        System.out.println(request.getId());
        Result result = null;
        final RpcInvocation invocation = (RpcInvocation) request.getData();
        Object serviceImpl = context.getBean(invocation.getClassName());
        final Wrapper wrapper = Wrapper.getWrapper(serviceImpl.getClass());
        Invoker<?> invoker = new WingServerInvoker<Object>(serviceImpl) {
            protected Object doInvoke(Object serviceImpl, String methodName,
                                      Class<?>[] parameterTypes,
                                      Object[] arguments) throws Throwable {
                return wrapper.invokeMethod(serviceImpl, methodName, parameterTypes,arguments);
            }
        };
        try {
           result =  invoker.invoke(invocation);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        response.setResult(result);
        return response;
    }
}
