package com.unic.server.codec;

import com.unic.core.base.RequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * 二次解码
 */
public class OrderProtocolDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.decode(byteBuf);

        out.add(requestMessage);
    }
}
