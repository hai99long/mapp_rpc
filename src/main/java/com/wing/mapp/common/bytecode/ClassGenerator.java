package com.wing.mapp.common.bytecode;

import com.wing.mapp.common.util.ClassHelper;
import com.wing.mapp.common.util.ReflectUtils;
import javassist.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by wanghl on 2017/3/26.
 */
public class ClassGenerator {
    private static final AtomicLong CLASS_NAME_COUNTER = new AtomicLong(0);
    private static final String SIMPLE_NAME_TAG = "<init>";
    private static final Map<ClassLoader, ClassPool> POOL_MAP = new ConcurrentHashMap<ClassLoader, ClassPool>();
    private CtClass mCtc;
    private ClassPool mPool;
    private String mClassName,mSuperClassName;
    private Set<String> mInterfaces;
    private List<String> mFields, mConstructors, mMethods;
    private boolean mDefaultConstructor = false;

    private ClassGenerator(ClassPool pool){
        this.mPool = pool;
    }
    public static ClassGenerator newInstance(ClassLoader classLoader){
        return new ClassGenerator(getClassPool(classLoader));
    }
    private static ClassPool getClassPool(ClassLoader classLoader){
        if(classLoader==null)
            return ClassPool.getDefault();
        ClassPool pool = POOL_MAP.get(classLoader);
        if(pool==null){
            pool = new ClassPool(true);
            pool.appendClassPath(new LoaderClassPath(classLoader));
            POOL_MAP.put(classLoader,pool);
        }
        return pool;
    }
    public ClassGenerator setClassName(String name) {
        mClassName = name;
        return this;
    }
    public Class<?> toClass(){
        if(mCtc!=null)
            mCtc.detach();
        try {
            CtClass ctSuperClass = mSuperClassName==null?null:mPool.get(mSuperClassName);
            if( mClassName == null )
                mClassName = ( mSuperClassName == null || javassist.Modifier.isPublic(ctSuperClass.getModifiers())
                    ? ClassGenerator.class.getName() : mSuperClassName + "$sc" ) + CLASS_NAME_COUNTER.getAndIncrement();
            mCtc = mPool.makeClass(mClassName);
            if(ctSuperClass!=null)
                mCtc.setSuperclass(ctSuperClass);
            if(mInterfaces!=null) {
                for (String itf : mInterfaces)
                    mCtc.addInterface(mPool.get(itf));
            }
            if(mFields!=null) {
                for (String field : mFields)
                    mCtc.addField(CtField.make(field, mCtc));
            }
            if(mMethods!=null){
                for(String method:mMethods)
                    mCtc.addMethod(CtMethod.make(method,mCtc));
            }
            if(mDefaultConstructor)
                mCtc.addConstructor(CtNewConstructor.defaultConstructor(mCtc));
            if( mConstructors != null ){
                for(String constructor:mConstructors){
                    String[] sn = mCtc.getSimpleName().split("\\$+");
                    mCtc.addConstructor(CtNewConstructor.make(constructor.replaceFirst(SIMPLE_NAME_TAG,sn[sn.length-1]),mCtc));
                }
            }
            return mCtc.toClass(ClassHelper.getCallerClassLoader(getClass()), null);
        }catch(RuntimeException e) {
            throw e;
        }
        catch(NotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        catch(CannotCompileException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    public ClassGenerator addConstructor(int mod, Class<?>[] parameterTypes, Class<?>[] exceptionTypes, String body){
        StringBuilder sb = new StringBuilder();
        sb.append(modifier(mod)).append(" ").append(SIMPLE_NAME_TAG);
        sb.append("(");
        for(int i=0; i<parameterTypes.length; i++){
            if(i>0)
                sb.append(",");
            sb.append(ReflectUtils.getName(parameterTypes[i]));
            sb.append(" args").append(i);
        }
        sb.append(")");
        if(exceptionTypes!=null && exceptionTypes.length>0){
            sb.append(" throws ");
            for(int i=0; i<exceptionTypes.length; i++){
                if(i>0)
                    sb.append(",");
                sb.append(ReflectUtils.getName(exceptionTypes[i]));
            }
        }
        sb.append("{").append(body).append("}");
        return addConstructor(sb.toString());
    }
    public ClassGenerator addConstructor(String code) {
        if( mConstructors == null )
            mConstructors = new LinkedList<String>();
        mConstructors.add(code);
        return this;
    }
    public ClassGenerator addDefaultConstructor() {
        mDefaultConstructor = true;
        return this;
    }
    public ClassGenerator addInterface(String interfaceName){
        if(mInterfaces==null)
            mInterfaces = new HashSet<String>();
        mInterfaces.add(interfaceName);
        return this;
    }
    public ClassGenerator addInterface(Class<?> itf){
        return addInterface(itf.getName());
    }
    public ClassGenerator setSuperClass(String cn) {
        mSuperClassName = cn;
        return this;
    }

    public ClassGenerator setSuperClass(Class<?> cl) {
        mSuperClassName = cl.getName();
        return this;
    }
    public ClassGenerator addMethod(String methodCode){
        if(mMethods==null)
            mMethods = new ArrayList<String>();
        mMethods.add(methodCode);
        return this;
    }
    public ClassGenerator addMethod(String methodName, int mod, Class<?> returnType, Class<?>[] parameterTypes, Class<?>[] exceptionTypes, String body){
        StringBuilder sb = new StringBuilder();
        sb.append(modifier(mod)).append(" ").append(ReflectUtils.getName(returnType)).append(" ").append(methodName);
        sb.append("(");
        for(int i=0; i<parameterTypes.length; i++){
            if(i>0)
                sb.append(",");
            sb.append(ReflectUtils.getName(parameterTypes[i]));
            sb.append(" args").append(i);
        }
        sb.append(")");
        if(exceptionTypes!=null && exceptionTypes.length>0){
            sb.append(" throws ");
            for(int i=0; i<exceptionTypes.length; i++){
                if(i>0)
                    sb.append(",");
                sb.append(ReflectUtils.getName(exceptionTypes[i]));
            }
        }
        sb.append("{").append(body).append("}");
        return addMethod(sb.toString());
    }
    private static String modifier(int mod) {
        if( java.lang.reflect.Modifier.isPublic(mod) ) return "public";
        if( java.lang.reflect.Modifier.isProtected(mod) ) return "protected";
        if( java.lang.reflect.Modifier.isPrivate(mod) ) return "private";
        return "";
    }
    public ClassGenerator addField(String code) {
        if( mFields == null )
            mFields = new ArrayList<String>();
        mFields.add(code);
        return this;
    }
    public void release() {
        if (mCtc != null) mCtc.detach();
        if (mInterfaces != null) mInterfaces.clear();
        if (mFields != null) mFields.clear();
        if (mMethods != null) mMethods.clear();
        if (mConstructors != null) mConstructors.clear();
    }
}
