package com.unic.server.handler;

import com.unic.core.auth.AuthOperation;
import com.unic.core.auth.AuthOperationResult;
import com.unic.core.base.RequestMessage;
import com.unic.core.operation.Operation;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 *
 * </p>
 *
 * @author linchengdong
 * @since 2022-08-15 16:33:11
 */
@ChannelHandler.Sharable
@Slf4j
public class AuthHandler extends SimpleChannelInboundHandler<RequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMessage msg) throws Exception {
        try{
            Operation operation = msg.getMessageBody();
            if (operation instanceof AuthOperation) {
                AuthOperation authOperation = AuthOperation.class.cast(operation);
                AuthOperationResult authOperationResult = authOperation.execute();
                if (authOperationResult.isPassAuth()) {
                    log.info("pass auth");
                } else {
                    log.error("fail to auth");
                    ctx.close();
                    return;
                }
            } else {
                log.error("expect first msg is auth");
                ctx.close();
                return;
            }
        } catch (Exception e) {
         log.error("auth happen error");
         ctx.close();
         return;
        }finally {
            // 一次授权， 就无需授权
            ctx.pipeline().remove(this);
        }

    }
}
