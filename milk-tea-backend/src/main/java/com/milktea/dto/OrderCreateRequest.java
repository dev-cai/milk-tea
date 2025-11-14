package com.milktea.dto;

import lombok.Data;

import java.util.List;

/**
 * 创建订单请求DTO
 * @author MilkTea Team
 */
@Data
public class OrderCreateRequest {
    
    private Long userId;
    private List<OrderItemRequest> items;
    private String remark;
    private Integer payType;
    
    @Data
    public static class OrderItemRequest {
        private Long productId;
        private Integer quantity;
        private Integer sweetness;
        private Integer temperature;
        private String toppings;
    }
}
