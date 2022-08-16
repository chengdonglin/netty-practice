package com.unic.server.codec;


import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author linchengdong
 */
public class OrderFrameDecoder extends LengthFieldBasedFrameDecoder {
    public OrderFrameDecoder() {
        super(Integer.MAX_VALUE, 0, 4, 0, 4);
    }
}
