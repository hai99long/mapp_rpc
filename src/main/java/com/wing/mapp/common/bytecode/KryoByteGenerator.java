package com.wing.mapp.common.bytecode;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.wing.mapp.common.codec.KryoHolder;
import com.wing.mapp.remoting.exchange.Request;
import com.wing.mapp.rpc.Invocation;
import com.wing.mapp.rpc.RpcInvocation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wanghl on 2017/4/17.
 */
public class KryoByteGenerator {
    /**
     * 序列化对象
     * @return
     */
    public byte[] serializationObject(){
        Kryo kryo = KryoHolder.get();

        byte[] bytes = null;
        for(int i=0; i<10000; i++){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Output output = new Output(baos);
            Request request = new Request();
            RpcInvocation invocation = new RpcInvocation();
            invocation.setClassName("hailong.com.hail1");
            request.setData(invocation);
            kryo.writeClassAndObject(output,request);
            output.flush();
            output.close();

            try {
                bytes = baos.toByteArray();
            } finally {
                try {
                    baos.flush();
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return bytes;
    }

    /**
     * 反序列化对象
     * @param bytes
     * @param clazz
     * @return
     */
    public RpcInvocation deserializationObject(byte[] bytes, Class clazz){
        System.out.println(bytes.length);
        Kryo kryo = KryoHolder.get();
        //kryo.setReferences(false);
        //kryo.register(clazz, new JavaSerializer());
        Request request=null;
        for(int i=0; i<10000; i++){
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            Input input = new Input(bais);
            request =  (Request) kryo.readClassAndObject(input);
            System.out.println(request.getId());
        }
        RpcInvocation invocation = (RpcInvocation)request.getData();
        System.out.println(invocation.getClassName());
       return invocation;
    }

    public static void main(String[] args){
        final  KryoByteGenerator kryoByteGenerator = new KryoByteGenerator();
        long start = System.currentTimeMillis();
        for(int i=1; i<2; i++){

            final  byte[] bytes = kryoByteGenerator.serializationObject();
           // System.out.println(bytes.length);
            new Thread(new Runnable() {
                public void run() {
                    kryoByteGenerator.deserializationObject(bytes,Request.class);
                }
            }).start();

        }
        long end = System.currentTimeMillis();
        System.out.println("======"+(end-start));
    }
}
