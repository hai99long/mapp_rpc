package com.wing.mapp.common.bytecode;

import com.wing.mapp.common.util.ReflectUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by wanghl on 2017/3/22.
 */
public abstract class Wrapper {
    private static AtomicLong WRAPPER_CLASS_COUNTER = new AtomicLong(0);
    private final static Map<Class<?>,Wrapper> WRAPPER_MAP = new ConcurrentHashMap<Class<?>, Wrapper>();

    /**
     * 根据Class得到它的包装类
     * @param c
     * @return
     */
    public static Wrapper getWrapper(Class<?> c){
        Wrapper wrapper = WRAPPER_MAP.get(c);
        if(wrapper==null) {
            wrapper = makeWrapper(c);
            WRAPPER_MAP.put(c,wrapper);
        }
        return wrapper;
    }
    /**
     * 用javassist实现的继承Wrapper的子类将实现invokeMethod方法
     * @param instance 运行的实例
     * @param mn        执行的方法名称
     * @param types     方法的参数类型
     * @param args      方法的参数值
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    abstract public Object invokeMethod(Object instance, String mn, Class<?>[] types, Object[] args) throws NoSuchMethodException, InvocationTargetException;
    private static Wrapper makeWrapper(Class<?> c){
        if( c.isPrimitive() )
            throw new IllegalArgumentException("Can not create wrapper for primitive type: " + c);
        long id = WRAPPER_CLASS_COUNTER.getAndIncrement();
        String name = c.getName();
        StringBuffer body = new StringBuffer();
        body.append("public Object invokeMethod(Object o, String n, Class[] p, Object[] v) throws java.lang.reflect.InvocationTargetException{");
        body.append(name).append(" w; try{ w = ((").append(name).append(")$1); }catch(Throwable e){ throw new IllegalArgumentException(e); }");
        Method[] methods = c.getMethods();
        if (hasMethods(methods)){
            body.append("try{");
            for(Method method:methods){
                String methodName = method.getName();
                body.append("if(\""+methodName+"\".equals($2) && ($3.length=="+method.getParameterTypes().length+")){");
                if( method.getReturnType() == Void.TYPE )
                    body.append(" w.").append(methodName).append('(').append(args(method.getParameterTypes(), "$4")).append(");").append(" return null;");
                else
                    body.append(" return ($w)w.").append(methodName).append('(').append(args(method.getParameterTypes(), "$4")).append(");");
                body.append("}");
            }
            body.append("} catch(Throwable e) {throw new java.lang.reflect.InvocationTargetException(e);}");
        }
        body.append("throw new NoSuchMethodException(\"Not found method \\\"\" + $2 + \"\\\" in class "+name+".\");}");
        ClassGenerator cc = ClassGenerator.newInstance(Wrapper.class.getClassLoader());
        cc.setClassName( Wrapper.class.getName() + id );
        cc.setSuperClass(Wrapper.class);
        cc.addDefaultConstructor();
        cc.addMethod(body.toString());
        try{
            Class<?> wc = cc.toClass();
            return (Wrapper)wc.newInstance();
        }
        catch(RuntimeException e) {
            throw e;
        }
        catch(Throwable e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        finally {
            cc.release();
        }
    }

    /**
     * 获取参数类型
     * @param cl
     * @param name
     * @return
     */
    private static String arg(Class<?> cl, String name) {
        if( cl.isPrimitive() ) {
            if( cl == Boolean.TYPE )
                return "((Boolean)" + name + ").booleanValue()";
            if( cl == Byte.TYPE )
                return "((Byte)" + name + ").byteValue()";
            if( cl == Character.TYPE )
                return "((Character)" + name + ").charValue()";
            if( cl == Double.TYPE )
                return "((Number)" + name + ").doubleValue()";
            if( cl == Float.TYPE )
                return "((Number)" + name + ").floatValue()";
            if( cl == Integer.TYPE )
                return "((Number)" + name + ").intValue()";
            if( cl == Long.TYPE )
                return "((Number)" + name + ").longValue()";
            if( cl == Short.TYPE )
                return "((Number)" + name + ").shortValue()";
            throw new RuntimeException("Unknown primitive type: " + cl.getName());
        }

        return "(" + ReflectUtils.getName(cl) + ")" + name;
    }

    /**
     * 拼接方法参数列表
     * @param cs
     * @param name
     * @return
     */
    private static String args(Class<?>[] cs,String name)
    {
        int len = cs.length;
        if( len == 0 ) return "";
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<len;i++)
        {
            if( i > 0 )
                sb.append(',');
            sb.append(arg(cs[i],name+"["+i+"]"));
        }
        return sb.toString();
    }

    /**
     * 判断是否还有方法，Object类的方法排除在外
     * @param methods
     * @return
     */
    private static boolean hasMethods(Method[] methods){
        if(methods==null || methods.length==0)
            return false;
        for(Method method:methods){
            if(method.getDeclaringClass()!=Object.class)
                return true;
        }
        return false;
    }

    public static void main(String[] args){
        String[] str1 = new String[]{"1","2"};
        String[] str2 = new String[]{"1","2"};
        String[][] strs = new String[][]{str1,str2};
        System.out.println(ReflectUtils.getName(strs.getClass()));
    }
}
