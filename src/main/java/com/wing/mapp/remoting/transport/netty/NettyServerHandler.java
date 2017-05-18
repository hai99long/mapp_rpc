package com.wing.mapp.remoting.transport.netty;

import com.wing.mapp.remoting.exchange.Request;
import com.wing.mapp.remoting.exchange.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by wanghl on 2017/4/8.
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<Request> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Request request) throws Exception {
        /*System.out.println("server client:"+request.getId());
        final RpcInvocation invocation = (RpcInvocation) request.getData();
        Result result = null;
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
            result = invoker.invoke(invocation);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }*/
        //StudentInfoService service = (StudentInfoService) Proxy.getProxy(StudentInfoService.class).newInstance(new InvokerInvocationHandler(invoker));
        /*Wrapper wrapper = Wrapper.getWrapper(Class.forName(invocation.getClassName()));
        StudentInfoServiceImpl testInterface = new StudentInfoServiceImpl();
        Class[] pc = new Class[]{String.class,int.class};
        Object[] abs = new Object[]{"khkh",12};
        try{
            wrapper.invokeMethod(testInterface,invocation.getMethodName(),invocation.getParameterTypes(),invocation.getArguments());
        }catch(Exception e){
            e.printStackTrace();
        }*/
        Response response = new Response(request.getId());
        /*response.setResult(result);
        channelHandlerContext.channel().writeAndFlush(response);*/

        //Response response = new Response();
        NettyServerInitializeTask recvTask = new NettyServerInitializeTask(request);
        NettyServer.submit(recvTask,channelHandlerContext,request);
    }
}
