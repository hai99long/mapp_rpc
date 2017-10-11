package com.wing.mapp.common.codec.protostuff;

import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by wanghl on 2017/5/28.
 */
public class SchemaCache {
    private static class SchemaCacheHolder{
        private static SchemaCache cache = new SchemaCache();
    }
    public static SchemaCache getInstance(){
        return SchemaCacheHolder.cache;
    }
    private static Cache<Class<?>,Schema<?>> cache = CacheBuilder.newBuilder()
            .maximumSize(1024).expireAfterWrite(1, TimeUnit.HOURS).build();

    private static Schema<?> get(final Class<?> cls,Cache<Class<?>,Schema<?>> cache){
        try {
            return cache.get(cls, new Callable<RuntimeSchema<?>>() {
                public RuntimeSchema<?> call() throws Exception {
                    return RuntimeSchema.createFrom(cls);
                }
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Schema<?> get(final Class<?> cls){
        return get(cls,cache);
    }
}
