package com.unic.core.order;

import com.unic.core.operation.OperationResult;
import lombok.Data;

/**
 * @author linchengdong
 */
@Data
public class OrderOperationResult extends OperationResult {

    private final int tableId;
    private final String dish;
    private final boolean complete;

}
