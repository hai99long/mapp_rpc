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
package com.wing.mapp.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @filename:MessageDecoder.java
 * @description:MessageDecoder功能模块
 * 目前不用，这个类用于解决粘包问题
 */
public class MessageDecoderBuf extends LengthFieldBasedFrameDecoder {

    private MessageCodecUtil util = null;

    public MessageDecoderBuf(final MessageCodecUtil util) {
        super(1048576, 0, 4, 0, 4);
        this.util = util;
    }
    public MessageDecoderBuf() {
        super(1048576, 0, 4, 0, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception
    {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null)
            return null;

       // return util.decode(frame);
        return null;
    }

    @Override
    protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length)
    {
        return buffer.slice(index, length);
    }
}

