package com.unic.core.order;


import com.unic.core.operation.Operation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author linchengdong
 */
@Data
@Slf4j
public class OrderOperation extends Operation {

    private int tableId;
    private String dish;

    public OrderOperation(int tableId, String dish) {
        this.tableId = tableId;
        this.dish = dish;
    }

    @Override
    public OrderOperationResult execute() {
        log.info("order's executing startup with orderRequest: " + toString());
        // 假设跟 spring 整合的时候，这里可以采用从 bean 容器获取到对应的 service 实例
        //execute order logic
        log.info("order's executing complete");
        OrderOperationResult orderResponse = new OrderOperationResult(tableId, dish, true);
        return orderResponse;
    }
}
