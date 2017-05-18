package com.wing.mapp.remoting.transport.netty.codec;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.wing.mapp.common.codec.KryoHolder;
import com.wing.mapp.rpc.Invocation;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

/**
 * Created by wanghl on 2017/4/16.
 */
public class RpcEncoder extends MessageToByteEncoder{
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object in, ByteBuf out) throws Exception {
        byte[] body = convertToBytes(in);  //将对象转换为byte
        int dataLength = body.length;     //读取消息的长度
        out.writeInt(dataLength);          //先将消息长度写入，也就是消息头
        out.writeBytes(body);              //消息体中包含我们要发送的数据
    }
    private byte[] convertToBytes(Object in) {
        ByteArrayOutputStream bos = null;
        Output output = null;
        Kryo kryo = KryoHolder.get();
        try {
            bos = new ByteArrayOutputStream();
            output = new Output(bos);
            kryo.writeObject(output, in);
            output.flush();
            return bos.toByteArray();
        } catch (KryoException e) {
            e.printStackTrace();
        }finally{
            IOUtils.closeQuietly(output);
            IOUtils.closeQuietly(bos);
        }
        return null;
    }
}
