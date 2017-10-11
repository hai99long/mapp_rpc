package com.wing.mapp.common.codec.protostuff;

import com.google.common.io.Closer;
import com.wing.mapp.common.codec.MessageCodecUtil;
import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by wanghl on 2017/6/1.
 */
public class ProtostuffCodecUtil implements MessageCodecUtil {
    private static Closer closer = Closer.create();
    private ProtostuffSerializePool pool = ProtostuffSerializePool.getInstance();
    private boolean rpcDirect = false;
    public boolean isRpcDirect() {
        return rpcDirect;
    }
    public void setRpcDirect(boolean rpcDirect) {
        this.rpcDirect = rpcDirect;
    }
    public void encode(ByteBuf out, Object message) throws IOException {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            closer.register(byteArrayOutputStream);
            ProtostuffSerialize serialize = pool.borrow();
            serialize.serialize(byteArrayOutputStream,message);
            byte[] body = byteArrayOutputStream.toByteArray();
            out.writeInt(body.length);
            out.writeBytes(body);
            pool.restore(serialize);
        }finally {
            closer.close();
        }
    }

    public Object decode(byte[] body) throws IOException {
        try{
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
            closer.register(byteArrayInputStream);
            ProtostuffSerialize serialize = pool.borrow();
            serialize.setRpcDirect(rpcDirect);
            Object object = serialize.deserialize(byteArrayInputStream);
            pool.restore(serialize);
            return object;
        }finally {
            closer.close();
        }
    }
}
