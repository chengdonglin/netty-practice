package com.unic.core.base;

import com.unic.core.operation.OperationResult;
import com.unic.core.operation.OperationType;

public class ResponseMessage extends Message<OperationResult> {
    @Override
    public Class getMessageBodyDecodeClass(int opcode) {
        return OperationType.fromOpCode(opcode).getOperationResultClazz();
    }
}
