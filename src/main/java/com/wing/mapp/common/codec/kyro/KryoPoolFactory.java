
package com.wing.mapp.common.codec.kyro;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.wing.mapp.remoting.exchange.Request;
import com.wing.mapp.remoting.exchange.Response;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 * @author wanghl
 * @filename:KryoPoolFactory.java
 * @description:KryoPoolFactory功能模块
 */
public class KryoPoolFactory {

    private static volatile KryoPoolFactory poolFactory = null;

    private KryoFactory factory = new KryoFactory() {
        public Kryo create() {
            Kryo kryo = new Kryo();
            kryo.setReferences(false);
            kryo.register(Request.class);  //注册请求对象
            kryo.register(Response.class); //注册响应对象
            kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
            return kryo;
        }
    };

    private KryoPool pool = new KryoPool.Builder(factory).build();

    private KryoPoolFactory() {
    }

    /**
     * 获取KryoPool实例
     * @return
     */
    public static KryoPool getKryoPoolInstance() {
        if (poolFactory == null) {
            synchronized (KryoPoolFactory.class) {
                if (poolFactory == null) {
                    poolFactory = new KryoPoolFactory();
                }
            }
        }
        return poolFactory.getPool();
    }

    private KryoPool getPool() {
        return pool;
    }
}

