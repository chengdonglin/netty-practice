package com.unic.server.codec;

import com.unic.core.base.ResponseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;


import java.util.List;

/**
 * 二次编码
 * @author DM
 */
public class OrderProtocolEncoder extends MessageToMessageEncoder<ResponseMessage> {


    @Override
    protected void encode(ChannelHandlerContext ctx, ResponseMessage responseMessage, List<Object> out) throws Exception {
        ByteBuf buffer = ctx.alloc().buffer();

        responseMessage.encode(buffer);

        out.add(buffer);
    }
}
