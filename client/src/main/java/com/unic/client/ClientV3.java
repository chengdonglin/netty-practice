package com.unic.client;

import cn.hutool.core.io.FileUtil;
import com.unic.client.codec.*;
import com.unic.core.auth.AuthOperation;
import com.unic.core.base.RequestMessage;
import com.unic.core.file.ImgUploadOperation;
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

public class ClientV3 {

    public static void main(String[] args) throws InterruptedException, ExecutionException, SSLException {

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);

        bootstrap.option(NioChannelOption.CONNECT_TIMEOUT_MILLIS, 10 * 1000);

        NioEventLoopGroup group = new NioEventLoopGroup();
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

                    pipeline.addLast(new OperationToRequestMessageEncoder());

                    pipeline.addLast(loggingHandler);


                }
            });

            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6668);

            channelFuture.sync();


            ChannelFuture future = channelFuture.channel().writeAndFlush(new RequestMessage(IdUtil.nextId(), new AuthOperation("admin", "123456")));

            future.get();

            byte[] bytes = FileUtil.readBytes("C:\\Users\\DM\\Desktop\\微信截图_20220512131302.png");

            ImgUploadOperation imgUploadOperation = new ImgUploadOperation("测试上传.png",bytes);

            RequestMessage message = new RequestMessage(IdUtil.nextId(), imgUploadOperation);
            channelFuture.channel().writeAndFlush(message);

            channelFuture.channel().closeFuture().sync();

        } finally{
            group.shutdownGracefully();
        }

    }

}
