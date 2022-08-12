package com.unic.server.codec;


import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author linchengdong
 */
public class OrderFrameDecoder extends LengthFieldBasedFrameDecoder {
    public OrderFrameDecoder() {
        super(10240, 0, 4, 0, 4);
    }
}
