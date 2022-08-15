package com.unic.server;


import com.unic.server.codec.OrderFrameDecoder;
import com.unic.server.codec.OrderFrameEncoder;
import com.unic.server.codec.OrderProtocolDecoder;
import com.unic.server.codec.OrderProtocolEncoder;
import com.unic.server.handler.AuthHandler;
import com.unic.server.handler.MetricHandler;
import com.unic.server.handler.OrderServerProcessHandler;
import com.unic.server.handler.ServerIdleHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import io.netty.handler.ipfilter.RuleBasedIpFilter;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;

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
        EventLoopGroup bossGroup = new NioEventLoopGroup(1,new DefaultThreadFactory("boss-"));
        EventLoopGroup workerGroup = new NioEventLoopGroup(0,new DefaultThreadFactory("work-"));
        UnorderedThreadPoolEventExecutor executors = new UnorderedThreadPoolEventExecutor(8, new DefaultThreadFactory("order-"));
        // 日志 handler
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.INFO);
        MetricHandler metricHandler = new MetricHandler();


        IpSubnetFilterRule filterRule = new IpSubnetFilterRule("127.1.0.1",16, IpFilterRuleType.REJECT);
        RuleBasedIpFilter ruleBasedIpFilter = new RuleBasedIpFilter(filterRule);

        AuthHandler authHandler = new AuthHandler();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(NioChannelOption.SO_BACKLOG,1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(NioChannelOption.TCP_NODELAY,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 添加日志 handler
                            ch.pipeline().addLast(loggingHandler);
                            // 黑白名单检测
                            ch.pipeline().addLast("ipfilter",ruleBasedIpFilter);

                            ch.pipeline().addLast("idleCheck",new ServerIdleHandler());



                            // 1. 拆包
                            ch.pipeline().addLast("frameDecoder",new OrderFrameDecoder());
                            ch.pipeline().addLast("frameEncoder",new OrderFrameEncoder());
                            //2. byte 转 Object 解码
                            ch.pipeline().addLast("protocolDecoder",new OrderProtocolDecoder());
                            ch.pipeline().addLast("protocolEncoder",new OrderProtocolEncoder());

                            // 可视化度量 handler
                            ch.pipeline().addLast("metricHandler",metricHandler);

                            ch.pipeline().addLast(authHandler); // 必须放在 OrderProtocolDecoder， 不然无法知道消息类型

                            // 3. 业务handler
                            ch.pipeline().addLast(executors,"business",new OrderServerProcessHandler());

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
