package com.unic.core.base;

import lombok.Data;

/**
 * 消息头
 * @author linchengdong
 */
@Data
public class MessageHeader {
    /**
     * 版本
     */
    private int version = 1;
    /**
     * 操作码
     */
    private int opCode;
    /**
     * 请求id
     */
    private long streamId;

}
