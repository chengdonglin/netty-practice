package com.unic.client.codec;


import io.netty.handler.codec.LengthFieldPrepender;

/**
 * 编码
 * @author DM
 */
public class OrderFrameEncoder extends LengthFieldPrepender {
    public OrderFrameEncoder() {
        super(4);
    }
}
