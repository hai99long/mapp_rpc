package com.wing.mapp.common.codec.protostuff;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.wing.mapp.remoting.exchange.Request;
import com.wing.mapp.remoting.exchange.Response;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by wanghl on 2017/5/31.
 */
public class ProtostuffSerialize {
    private static SchemaCache schemaCache = SchemaCache.getInstance();
    private static Objenesis objenesis = new ObjenesisStd(true);
    private boolean rpcDirect = false;
    public boolean isRpcDirect() {
        return rpcDirect;
    }
    public void setRpcDirect(boolean rpcDirect) {
        this.rpcDirect = rpcDirect;
    }

    private static <T> Schema<T> getSchema(Class<T> cls){
        return (Schema<T>)schemaCache.get(cls);
    }
    public Object deserialize(InputStream input){
        try{
            Class cls = isRpcDirect() ? Request.class : Response.class;
            Object message = objenesis.newInstance(cls);
            Schema<Object> schema = getSchema(cls);
            ProtobufIOUtil.mergeFrom(input,message,schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
    public void serialize(OutputStream output, Object object){
        Class cls = object.getClass();
        LinkedBuffer linkedBuffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try{
            Schema schema = getSchema(cls);
            ProtobufIOUtil.writeTo(output,object,schema,linkedBuffer);
        }catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
