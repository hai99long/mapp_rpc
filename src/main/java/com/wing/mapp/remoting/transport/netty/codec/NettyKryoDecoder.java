package com.wing.mapp.remoting.transport.netty.codec;

import com.wing.mapp.common.codec.KryoSerializer;
import com.wing.mapp.common.codec.kyro.KryoCodecUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class NettyKryoDecoder extends LengthFieldBasedFrameDecoder
{
    private KryoCodecUtil util = null;

    public NettyKryoDecoder(final KryoCodecUtil util) {
        super(1048576, 0, 4, 0, 4);
        this.util = util;
    }
	public NettyKryoDecoder()
    {
        super(1048576, 0, 4, 0, 4);
    }

	@Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception 
    {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) 
            return null;
                
        return KryoSerializer.deserialize(frame);
    }

    @Override
    protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) 
    {
        return buffer.slice(index, length);
    }
}
