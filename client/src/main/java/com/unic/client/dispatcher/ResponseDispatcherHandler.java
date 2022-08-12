package com.unic.client.dispatcher;

import com.unic.core.base.ResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * <p>
 *  ResponseMessage 映射到 RequestPendingCenter 的 handler
 * </p>
 *
 * @author linchengdong
 * @since 2022-08-12 14:41:55
 */
public class ResponseDispatcherHandler extends SimpleChannelInboundHandler<ResponseMessage>  {


    private RequestPendingCenter requestPendingCenter;

    public ResponseDispatcherHandler(RequestPendingCenter requestPendingCenter) {
        this.requestPendingCenter = requestPendingCenter;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponseMessage responseMessage) throws Exception {
        requestPendingCenter.set(responseMessage.getMessageHeader().getStreamId(),responseMessage.getMessageBody());
    }
}
