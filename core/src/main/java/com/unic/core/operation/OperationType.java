package com.unic.core.operation;



import com.unic.core.auth.AuthOperation;
import com.unic.core.auth.AuthOperationResult;
import com.unic.core.file.ImgUploadOperation;
import com.unic.core.file.ImgUploadOperationResult;
import com.unic.core.keepalive.KeepaliveOperation;
import com.unic.core.keepalive.KeepaliveOperationResult;
import com.unic.core.order.OrderOperation;
import com.unic.core.order.OrderOperationResult;

import java.util.function.Predicate;

/**
 * @author DM
 */

public enum OperationType {

    AUTH(1, AuthOperation.class, AuthOperationResult.class),
    KEEPALIVE(2, KeepaliveOperation.class, KeepaliveOperationResult.class),
    ORDER(3, OrderOperation.class, OrderOperationResult.class),
    IMG(4, ImgUploadOperation.class, ImgUploadOperationResult.class);

    private int opCode;
    private Class<? extends Operation> operationClazz;
    private Class<? extends OperationResult> operationResultClazz;

    OperationType(int opCode, Class<? extends Operation> operationClazz, Class<? extends OperationResult> responseClass) {
        this.opCode = opCode;
        this.operationClazz = operationClazz;
        this.operationResultClazz = responseClass;
    }

    public int getOpCode(){
        return opCode;
    }

    public Class<? extends Operation> getOperationClazz() {
        return operationClazz;
    }

    public Class<? extends OperationResult> getOperationResultClazz() {
        return operationResultClazz;
    }

    public static OperationType fromOpCode(int type){
        return getOperationType(requestType -> requestType.opCode == type);
    }

    public static OperationType fromOperation(Operation operation){
        return getOperationType(requestType -> requestType.operationClazz == operation.getClass());
    }

    private static OperationType getOperationType(Predicate<OperationType> predicate){
        OperationType[] values = values();
        for (OperationType operationType : values) {
            if(predicate.test(operationType)){
                return operationType;
            }
        }

        throw new AssertionError("no found type");
    }

}
