package com.unic.core.keepalive;

import com.unic.core.operation.OperationResult;
import lombok.Data;

@Data
public class KeepaliveOperationResult extends OperationResult {

    private final long time;

}
