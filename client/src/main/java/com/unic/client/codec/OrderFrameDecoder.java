package com.unic.client.codec;


import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 解码
 */
public class OrderFrameDecoder extends LengthFieldBasedFrameDecoder {
    public OrderFrameDecoder() {
        super(Integer.MAX_VALUE, 0, 4, 0, 4);
    }
}
