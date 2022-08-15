package com.unic.client;

import com.unic.client.codec.*;
import com.unic.client.dispatcher.*;
import com.unic.core.base.RequestMessage;
import com.unic.core.operation.OperationResult;
import com.unic.core.order.OrderOperation;
import com.unic.core.util.IdUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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

        KeepaliveHandler keepaliveHandler = new KeepaliveHandler();

        RequestPendingCenter requestPendingCenter = new RequestPendingCenter();
        try {

            bootstrap.group(group);

            final LoggingHandler loggingHandler = new LoggingHandler(LogLevel.INFO);

            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    pipeline.addLast(new ClientIdleCheckHandler());

                    pipeline.addLast("frameDecoder",new OrderFrameDecoder());
                    pipeline.addLast("frameEncoder",new OrderFrameEncoder());

                    pipeline.addLast("protocolEncoder",new OrderProtocolEncoder());
                    pipeline.addLast("protocolDecoder",new OrderProtocolDecoder());

                    pipeline.addLast("resultDispatcher",new ResponseDispatcherHandler(requestPendingCenter));

                    pipeline.addLast("requestMessageEncoder",new OperationToRequestMessageEncoder());

                    pipeline.addLast(keepaliveHandler);

                    pipeline.addLast("logHandler",loggingHandler);


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
                ChannelFuture future = channelFuture.channel().writeAndFlush(requestMessage);
                // 添加发送结果回调监听
                future.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            requestPendingCenter.remove(streamId);
                        }
                    }
                });
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
