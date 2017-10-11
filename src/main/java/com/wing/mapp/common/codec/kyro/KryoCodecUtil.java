/**
 * Copyright (C) 2016 Newland Group Holding Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wing.mapp.common.codec.kyro;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.google.common.io.Closer;
import com.wing.mapp.common.codec.MessageCodecUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @filename:KryoCodecUtil.java
 * @description:KryoCodecUtil功能模块
 */
public class KryoCodecUtil implements MessageCodecUtil{

    private KryoPool pool;
    private static Closer closer = Closer.create();

    public KryoCodecUtil(KryoPool pool) {
        this.pool = pool;
    }

    /**
     * 编码，将对象编码为ByteBuf
     * @param outBuffer
     * @param message
     * @throws IOException
     */
    public void encode(final ByteBuf outBuffer, final Object message) throws IOException {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            closer.register(byteArrayOutputStream);
            KryoSerialize kryoSerialization = new KryoSerialize(pool);
            kryoSerialization.serialize(byteArrayOutputStream, message);
            byte[] body = byteArrayOutputStream.toByteArray();
            int dataLength = body.length;
            outBuffer.writeInt(dataLength);
            outBuffer.writeBytes(body);
        } finally {
            closer.close();
        }
    }

    /**
     * 解码，将byte数组解码为message对象
     * @param body
     * @return
     * @throws IOException
     */
    public Object decode(byte[] body) throws IOException {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
            closer.register(byteArrayInputStream);
            KryoSerialize kryoSerialization = new KryoSerialize(pool);
            Object obj = kryoSerialization.deserialize(byteArrayInputStream);
            return obj;
        } finally {
            closer.close();
        }
    }
    public Object decode(final ByteBuf byteBuf) throws IOException {
        try {
            if(byteBuf == null)
                return null;
            Input input = new Input(new ByteBufInputStream(byteBuf));
            closer.register(input);
            KryoSerialize kryoSerialization = new KryoSerialize(pool);
            Object obj = kryoSerialization.deserialize(input);
            return obj;
        } finally {
            closer.close();
        }
    }
}
