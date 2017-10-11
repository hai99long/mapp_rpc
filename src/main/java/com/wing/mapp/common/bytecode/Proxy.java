package com.wing.mapp.common.bytecode;

import com.wing.mapp.common.util.ClassHelper;
import com.wing.mapp.common.util.ReflectUtils;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by wanghl on 2017/3/25.
 * 客户端调用时根据接口生成的代理类
 */
public abstract class Proxy {
    private static final AtomicLong proxyClassCounter = new AtomicLong(0);
    private static final String prxoyPackageName = Proxy.class.getPackage().getName();
    private static final Map<ClassLoader, Map<String, Object>> proxyCacheMap = new WeakHashMap<ClassLoader, Map<String, Object>>();
    private static final Object pendingGenerationMarker = new Object();

    public static Proxy getProxy(Class<?>... interfaces) {
        return getProxy(ClassHelper.getCallerClassLoader(Proxy.class), interfaces);
    }
    /**
     * 创建代理类
     */
    public static Proxy getProxy(ClassLoader classLoader,Class<?>... interfaces){
        if( interfaces.length > 65535 )
            throw new IllegalArgumentException("interface limit exceeded");
        Set interfaceSet = new HashSet();
        StringBuilder sb = new StringBuilder();
        for(Class<?> itf:interfaces){
            String interfaceName = itf.getName();
            //如果你传入的不是接口抛出异常
            if(!itf.isInterface()){
                throw new RuntimeException(itf + " is not a interface.");
            }
            Class interfaceClass = null;
            try {
                //加载每一个接口的运行时Class信息
                interfaceClass = Class.forName(interfaceName, false, classLoader);
            } catch (ClassNotFoundException e) {
            }
            //如果采用你传入的类加载器载入的Class和你传入的Class不相等则抛出异常
            if (interfaceClass != itf) {
                throw new IllegalArgumentException(itf
                        + " is not visible from class loader");
            }
            if (interfaceSet.contains(interfaceClass)) {
                throw new IllegalArgumentException("repeated interface: "
                        + interfaceClass.getName());
            }
            interfaceSet.add(interfaceClass);
            sb.append(interfaceName).append(";");
        }
        // 用接口名字作为代理类缓存的可以值，我们需要接口名字去分辨类加载器.
        String key = sb.toString();
        //通过ClassLoader获得缓存
        Map<String, Object> cache;
        synchronized (proxyCacheMap){
            //这个是为了存储每一个类加载器所载入过的代理接口的代理类
            cache = (Map) proxyCacheMap.get(classLoader);
            if (cache == null) {
                cache = new HashMap<String,Object>();
                proxyCacheMap.put(classLoader, cache);
            }
        }
        Proxy proxy = null;
        synchronized (cache){
            do{
                //检查是否有生成好的代理
                Object value = cache.get(key);
                if (value instanceof Reference<?> ) {
                    proxy = (Proxy)((Reference<?>) value).get();
                }
                //有的话直接返回
                if (proxy != null) {
                    //代理类已经存在，返回
                    return proxy;
                    //否则看一下这个代理类是不是正在构造中，是的话就在cache对象上等待
                } else if (value == pendingGenerationMarker) {
                    //代理类正在被创建，等待
                    try {
                        cache.wait();
                    } catch (InterruptedException e) {
                        /*
                         * 对于代理类的生成，我们其实只会等待很短的时间，所以在这里我们可以安全的忽略这个异常的发生
                         */
                    }
                    continue;
                    //如果没有现成的，也没有创造中的，那就开始创造代理类
                } else {
                    //将当前代理类置为正在构造中，并直接退出循环
                    cache.put(key, pendingGenerationMarker);
                    break;
                }
            }while (true);
        }
        long id = proxyClassCounter.getAndIncrement();
        String proxyPkg  = null;
        ClassGenerator interfaceImplement = null, proxyClass = null;
        try{
            interfaceImplement = ClassGenerator.newInstance(classLoader);
            Set<String> worked = new HashSet<String>();
            List<Method> methodList = new ArrayList<Method>();
            for(Class<?> itf:interfaces){
                //这一段是看你传入的接口中有没有不是public的接口，如果有，这些接口必须全部在一个包里定义的，否则抛异常
                if (!Modifier.isPublic(itf.getModifiers())) {
                    String pkg = itf.getPackage().getName();
                    if (proxyPkg  == null) {
                        proxyPkg  = pkg;
                    } else if (!pkg.equals(proxyPkg)) {
                        throw new IllegalArgumentException(
                                "non-public interfaces from different packages");
                    }
                }
                interfaceImplement.addInterface(itf);
                for(Method method:itf.getMethods()){
                    String desc = ReflectUtils.getDesc(method);
                    if( worked.contains(desc) )
                        continue;
                    worked.add(desc);
                    Class<?> rt = method.getReturnType();
                    Class<?>[] pts = method.getParameterTypes();
                    StringBuilder code = new StringBuilder("Object[] args = new Object[").append(pts.length).append("];");
                    for(int j=0;j<pts.length;j++)
                        code.append(" args[").append(j).append("] = ($w)$").append(j+1).append(";");
                    code.append("Object ret = handler.invoke(this,methods[" + methodList.size() + "], args);");
                    if( !Void.TYPE.equals(rt) )
                        code.append(" return ").append(asArgument(rt, "ret")).append(";");
                    methodList.add(method);
                    interfaceImplement.addMethod(method.getName(),method.getModifiers(), rt, pts, method.getExceptionTypes(), code.toString());
                }
            }
            if(proxyPkg==null)
                proxyPkg = prxoyPackageName;
            String proxyClassName = proxyPkg + ".proxy" + id;
            interfaceImplement.setClassName(proxyClassName);
            interfaceImplement.addField("public static java.lang.reflect.Method[] methods;");
            interfaceImplement.addField("private " + InvocationHandler.class.getName() + " handler;");
            interfaceImplement.addConstructor(Modifier.PUBLIC, new Class<?>[]{ InvocationHandler.class }, new Class<?>[0], "handler=$1;");
            interfaceImplement.addDefaultConstructor();
            interfaceImplement.toClass().getField("methods").set(null,methodList.toArray(new Method[0]));
            //创建代理类
            String fcn = Proxy.class.getName() + id;
            proxyClass = ClassGenerator.newInstance(classLoader);
            proxyClass.setClassName(fcn);
            proxyClass.addDefaultConstructor();
            proxyClass.setSuperClass(Proxy.class);
            proxyClass.addMethod("public Object newInstance(" + InvocationHandler.class.getName() + " h){ return new " + proxyClassName + "($1); }");
            proxy = (Proxy)proxyClass.toClass().newInstance();
        }catch(RuntimeException e) {
            throw e;
        }
        catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }finally {
            if( interfaceImplement != null )
                interfaceImplement.release();
            if( proxyClass != null )
                proxyClass.release();
            synchronized( cache ) {
                if( proxy == null )
                    cache.remove(key);
                else
                    cache.put(key, new WeakReference<Proxy>(proxy));
                cache.notifyAll();
            }
        }
        return proxy;
    }

    /**
     * 获得方法参数类型
     * @param returnType 返回类型
     * @param returnArgName 返回参数名字
     * @return
     */
    private static String asArgument(Class<?> returnType, String returnArgName) {
        if (returnType.isPrimitive()) {
            if (Boolean.TYPE == returnType)
                return returnArgName + "==null?false:((Boolean)" + returnArgName + ").booleanValue()";
            if (Byte.TYPE == returnType)
                return returnArgName + "==null?(byte)0:((Byte)" + returnArgName + ").byteValue()";
            if (Character.TYPE == returnType)
                return returnArgName + "==null?(char)0:((Character)" + returnArgName + ").charValue()";
            if (Double.TYPE == returnType)
                return returnArgName + "==null?(double)0:((Double)" + returnArgName + ").doubleValue()";
            if (Float.TYPE == returnType)
                return returnArgName + "==null?(float)0:((Float)" + returnArgName + ").floatValue()";
            if (Integer.TYPE == returnType)
                return returnArgName + "==null?(int)0:((Integer)" + returnArgName + ").intValue()";
            if (Long.TYPE == returnType)
                return returnArgName + "==null?(long)0:((Long)" + returnArgName + ").longValue()";
            if (Short.TYPE == returnType)
                return returnArgName + "==null?(short)0:((Short)" + returnArgName + ").shortValue()";
            throw new RuntimeException(returnArgName + " is unknown primitive type.");
        }
        return "(" + ReflectUtils.getName(returnType) + ")" + returnArgName;
    }

    /**
     * get instance with special handler.
     *
     * @return instance.
     */
    abstract public Object newInstance(InvocationHandler handler);
}
