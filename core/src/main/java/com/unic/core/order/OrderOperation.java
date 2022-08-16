package com.unic.core.order;


import com.unic.core.operation.Operation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 *
 @Slf4j
 @Component
 public class BootNettyChannelInboundHandlerAdapter extends ChannelInboundHandlerAdapter {
    public static BootNettyChannelInboundHandlerAdapter bootNettyChannelInboundHandlerAdapter;
    //1.正常注入[记得主类也需要使用@Component注解]
    @Autowired
    private DeviceUpService deviceUpService;
 //2.初始化构造方法一定要有
    public BootNettyChannelInboundHandlerAdapter() {

    }
 //3.容器初始化的时候进行执行-这里是重点
 @PostConstruct
 public void init() {
    bootNettyChannelInboundHandlerAdapter = this;
    bootNettyChannelInboundHandlerAdapter.deviceUpService = this.deviceUpService;
 }

 */

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
