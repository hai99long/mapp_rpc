package com.wing.mapp.remoting.transport.netty.codec;

import com.wing.mapp.common.codec.KryoSerializer;
import com.wing.mapp.common.codec.kyro.KryoCodecUtil;
import com.wing.mapp.common.codec.kyro.KryoPoolFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NettyKryoEncoder extends MessageToByteEncoder<Object>
{
	private KryoCodecUtil util = null;

	public NettyKryoEncoder(final KryoCodecUtil util) {
		this.util = util;
	}
	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out)
			throws Exception 
	{
		/*KryoCodecUtil util = new KryoCodecUtil(KryoPoolFactory.getKryoPoolInstance());
		util.encode(out, msg);*/
		KryoSerializer.serialize(msg, out);
	}
}
