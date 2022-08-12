package com.unic.client;

import com.unic.client.codec.*;
import com.unic.client.dispatcher.OperationResultFuture;
import com.unic.client.dispatcher.RequestPendingCenter;
import com.unic.client.dispatcher.ResponseDispatcherHandler;
import com.unic.core.base.RequestMessage;
import com.unic.core.operation.OperationResult;
import com.unic.core.order.OrderOperation;
import com.unic.core.util.IdUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import javax.net.ssl.SSLException;
import java.util.concurrent.ExecutionException;

public class ClientV2 {

    public static void main(String[] args) throws InterruptedException, ExecutionException, SSLException {

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);

        bootstrap.option(NioChannelOption.CONNECT_TIMEOUT_MILLIS, 10 * 1000);

        NioEventLoopGroup group = new NioEventLoopGroup();

        RequestPendingCenter requestPendingCenter = new RequestPendingCenter();
        try {

            bootstrap.group(group);

            final LoggingHandler loggingHandler = new LoggingHandler(LogLevel.INFO);

            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    pipeline.addLast(new OrderFrameDecoder());
                    pipeline.addLast(new OrderFrameEncoder());

                    pipeline.addLast(new OrderProtocolEncoder());
                    pipeline.addLast(new OrderProtocolDecoder());

                    pipeline.addLast(new ResponseDispatcherHandler(requestPendingCenter));

                    pipeline.addLast(new OperationToRequestMessageEncoder());

                    pipeline.addLast(loggingHandler);


                }
            });

            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6668);

            channelFuture.sync();

            long streamId = IdUtil.nextId();

            RequestMessage requestMessage = new RequestMessage(streamId,
                    new OrderOperation(1001,"南瓜"));

            OperationResultFuture operationResultFuture = new OperationResultFuture();

            // streamId 绑定 OperationResultFuture
            requestPendingCenter.add(streamId,operationResultFuture);

            try {
                channelFuture.channel().writeAndFlush(requestMessage);

                OperationResult operationResult = operationResultFuture.get();

                System.out.println(operationResult.toString());
            } catch (Exception e) {
                requestPendingCenter.remove(streamId);
            }


            channelFuture.channel().closeFuture().sync();

        } finally{
            group.shutdownGracefully();
        }

    }

}
