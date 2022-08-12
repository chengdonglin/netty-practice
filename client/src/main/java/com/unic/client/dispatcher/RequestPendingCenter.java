package com.unic.client.dispatcher;

import com.unic.core.operation.OperationResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * </p>
 *
 * @author linchengdong
 * @since 2022-08-12 14:37:15
 */
public class RequestPendingCenter {
    private Map<Long,OperationResultFuture> map = new ConcurrentHashMap<>();


    public void add(Long streamId, OperationResultFuture future) {
        this.map.put(streamId,future);
    }


    public void set(Long streamId, OperationResult operationResult) {
        OperationResultFuture operationResultFuture = this.map.get(streamId);
        if (operationResultFuture != null) {
            operationResultFuture.setSuccess(operationResult);
        }
        this.map.remove(streamId);
    }

    public void remove(Long streamId) {
        this.map.remove(streamId);
    }

}
