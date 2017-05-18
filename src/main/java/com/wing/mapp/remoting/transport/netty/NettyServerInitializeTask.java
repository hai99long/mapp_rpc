package com.wing.mapp.remoting.transport.netty;

import java.util.concurrent.Callable;

import com.wing.mapp.common.bytecode.Wrapper;
import com.wing.mapp.remoting.exchange.Request;
import com.wing.mapp.remoting.exchange.Response;
import com.wing.mapp.rpc.Invoker;
import com.wing.mapp.rpc.Result;
import com.wing.mapp.rpc.RpcInvocation;
import com.wing.mapp.rpc.protocol.WingServerInvoker;
import com.wing.mapp.sample.StudentInfoServiceImpl;

/**
 * Created by wanghl on 2017/5/6.
 */
public class NettyServerInitializeTask implements Callable<Response> {
    private Request request;

    public NettyServerInitializeTask(Request request){
        this.request = request;
    }

    public Response call() throws Exception {
        Response response = new Response(request.getId());
        System.out.println("server client:"+request.getId());
        Result result = null;
        final RpcInvocation invocation = (RpcInvocation) request.getData();
        StudentInfoServiceImpl studentInfoService = new StudentInfoServiceImpl();
        final Wrapper wrapper = Wrapper.getWrapper(StudentInfoServiceImpl.class);
        Invoker<StudentInfoServiceImpl> invoker = new WingServerInvoker<StudentInfoServiceImpl>(studentInfoService) {
            protected Object doInvoke(StudentInfoServiceImpl studentInfoService, String methodName,
                                      Class<?>[] parameterTypes,
                                      Object[] arguments) throws Throwable {
                return wrapper.invokeMethod(studentInfoService, methodName, parameterTypes,arguments);
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
