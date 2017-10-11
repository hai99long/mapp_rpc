package com.wing.mapp.common.codec.kyro;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @filename:KryoSerialize.java
 * @description:KryoSerialize功能模块
 * Kryo序列化。它是针对Java，而定制实现的高效对象序列化框架，
 * 相比Java本地原生序列化方式，Kryo在处理性能上、码流大小上等等方面有很大的优化改进。
 * 出于应对高并发场景下，频繁地创建、销毁序列化对象，会非常消耗JVM的内存资源、以及时间。
 * Kryo3以后的版本中集成引入了序列化对象池功能模块（KryoFactory、KryoPool），
 * 这样我们就不必再利用Apache Commons Pool对其进行二次封装
 */
public class KryoSerialize  {

    private KryoPool pool = null;

    public KryoSerialize(final KryoPool pool) {
        this.pool = pool;
    }

    /**
     * 序列化对象
     * @param output
     * @param object
     * @throws IOException
     */
    public void serialize(OutputStream output, Object object) throws IOException {
        Kryo kryo = pool.borrow();
        Output out = new Output(output);
        try{
            kryo.writeClassAndObject(out, object);
        }finally {
            out.close();
            output.close();
            pool.release(kryo);
        }
    }

    /**
     * 反序列化对象
     * @param input
     * @return
     * @throws IOException
     */
    public Object deserialize(InputStream input) throws IOException {
        Kryo kryo = pool.borrow();
        Input in = new Input(input);
        try{
            Object result = kryo.readClassAndObject(in);
            return result;
        }finally {
            in.close();
            input.close();
            pool.release(kryo);
        }
    }
}
