package com.unic.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  空闲检测 handler , 10s 检测一次
 * </p>
 *
 * @author linchengdong
 * @since 2022-08-15 16:01:45
 */
@Slf4j
public class ServerIdleHandler extends IdleStateHandler {
    public ServerIdleHandler() {
        super(10, 0, 0, TimeUnit.SECONDS);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        if (evt == IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT) {
            log.info("idle check happen, so close channel");
            ctx.close();
            return;
        }
        super.channelIdle(ctx, evt);
    }
}
