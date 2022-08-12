package com.unic.core.operation;

import com.unic.core.base.MessageBody;

/**
 * @author linchengdong
 */
public abstract class Operation extends MessageBody {

    /**
     * 操作执行
     * @return
     */
    public abstract OperationResult execute();

}
