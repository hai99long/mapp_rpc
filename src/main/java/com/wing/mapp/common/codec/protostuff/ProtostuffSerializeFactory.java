package com.wing.mapp.common.codec.protostuff;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * Created by wanghl on 2017/5/31.
 */
public class ProtostuffSerializeFactory extends BasePooledObjectFactory<ProtostuffSerialize> {
    public ProtostuffSerialize create() throws Exception {
        return new ProtostuffSerialize();
    }

    public PooledObject<ProtostuffSerialize> wrap(ProtostuffSerialize protostuffSerialize) {
        return new DefaultPooledObject<ProtostuffSerialize>(protostuffSerialize);
    }
}
