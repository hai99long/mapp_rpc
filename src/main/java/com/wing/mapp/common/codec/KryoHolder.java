package com.wing.mapp.common.codec;

import com.esotericsoftware.kryo.Kryo;

/**
 * Created by wanghl on 2017/4/17.
 */
public class KryoHolder {
    private static ThreadLocal<Kryo> threadLocalKryo = new ThreadLocal<Kryo>() {
        protected Kryo initialValue() {
            Kryo kryo = new KryoReflectionFactory();
            return kryo;
        };
    };

    public static Kryo get() {
        return threadLocalKryo.get();
    }
}
