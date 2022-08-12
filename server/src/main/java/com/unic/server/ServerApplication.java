package com.unic.server;


import com.unic.server.codec.OrderFrameDecoder;
import com.unic.server.codec.OrderFrameEncoder;
import com.unic.server.codec.OrderProtocolDecoder;
import com.unic.server.codec.OrderProtocolEncoder;
import com.unic.server.handler.OrderServerProcessHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * <p>
 *
 * </p>
 *
 * @author linchengdong
 * @since 2022-08-11 17:07:03
 */
public class ServerApplication {
    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        // 日志 handler
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.INFO);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 添加日志 handler
                            ch.pipeline().addLast(loggingHandler);
                            // 1. 拆包
                            ch.pipeline().addLast("frameDecoder",new OrderFrameDecoder());
                            ch.pipeline().addLast("frameEncoder",new OrderFrameEncoder());
                            //2. byte 转 Object 解码
                            ch.pipeline().addLast("protocolDecoder",new OrderProtocolDecoder());
                            ch.pipeline().addLast("protocolEncoder",new OrderProtocolEncoder());

                            // 3. 业务handler
                            ch.pipeline().addLast(workerGroup,"business",new OrderServerProcessHandler());

                        }
                    });
            System.out.println(".....服务器 is ready...");
            ChannelFuture cf = bootstrap.bind(6668).sync();
            cf.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (cf.isSuccess()) {
                        System.out.println("监听端口 6668 成功");
                    } else {
                        System.out.println("监听端口 6668 失败");
                    }
                }
            });
            cf.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
