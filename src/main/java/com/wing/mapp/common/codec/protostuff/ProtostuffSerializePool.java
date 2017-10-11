package com.wing.mapp.common.codec.protostuff;

import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * Created by wanghl on 2017/5/31.
 */
public class ProtostuffSerializePool {
    private GenericObjectPool<ProtostuffSerialize> protostuffPool;
    private static volatile ProtostuffSerializePool instance;

    private ProtostuffSerializePool(){
        protostuffPool = new GenericObjectPool<ProtostuffSerialize>(new ProtostuffSerializeFactory());
    }
    public GenericObjectPool<ProtostuffSerialize> getProtostuffPool() {
        return protostuffPool;
    }
    public static ProtostuffSerializePool getInstance(){
        if(instance==null){
            synchronized (ProtostuffSerializePool.class){
                if(instance==null){
                    instance = new ProtostuffSerializePool();
                }
            }
        }
        return instance;
    }
    public ProtostuffSerialize borrow(){
        try {
            return getProtostuffPool().borrowObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public void restore(final ProtostuffSerialize object){
        getProtostuffPool().returnObject(object);
    }
}
