package com.unic.client.dispatcher;

import io.netty.handler.timeout.IdleStateHandler;

/**
 * <p>
 *
 * </p>
 *
 * @author linchengdong
 * @since 2022-08-15 16:11:07
 */
public class ClientIdleCheckHandler extends IdleStateHandler {
    public ClientIdleCheckHandler() {
        super(0,5,0);
    }


}
