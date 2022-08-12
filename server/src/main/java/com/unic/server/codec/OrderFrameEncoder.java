package com.unic.server.codec;


import io.netty.handler.codec.LengthFieldPrepender;

/**
 * 编码
 */
public class OrderFrameEncoder extends LengthFieldPrepender {
    public OrderFrameEncoder() {
        super(4);
    }
}
