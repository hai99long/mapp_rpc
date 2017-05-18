package com.wing.mapp.remoting.transport.netty.codec;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.wing.mapp.common.codec.KryoHolder;
import com.wing.mapp.rpc.Invocation;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by wanghl on 2017/4/16.
 */
public class RpcDecoder extends ByteToMessageDecoder{
    private final static int HEAD_LENGTH = 4;
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //这个HEAD_LENGTH是我们用于表示头长度的字节数。  由于上面我们传的是一个int类型的值，所以这里HEAD_LENGTH的值为4.
        if (in.readableBytes() < HEAD_LENGTH) {
            return;
        }
        in.markReaderIndex();           //我们标记一下当前的readIndex的位置
        int dataLength = in.readInt(); // 读取传送过来的消息的长度。ByteBuf 的readInt()方法会让他的readIndex增加4
        if (dataLength < 0) {          // 我们读到的消息体长度为0，这是不应该出现的情况，这里出现这情况，关闭连接。
            ctx.close();
        }
        //读到的消息体长度如果小于我们传送过来的消息长度，则resetReaderIndex. 这个配合markReaderIndex使用的。
        // 把readIndex重置到mark的地方
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        Object o = convertToObject(in);      //将byte数据转化为我们需要的对象。伪代码，用什么序列化，自行选择
        out.add(o);
    }

    private Object convertToObject(ByteBuf in) {
        Input input = new Input(new ByteBufInputStream(in));
        try {
            Kryo kryo = KryoHolder.get();
            return kryo.readClassAndObject(input);
        } catch (KryoException e) {
            e.printStackTrace();
        }finally{
            IOUtils.closeQuietly(input);
        }
        return null;
    }
}
