package com.milktea.dto;

import lombok.Data;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 创建订单请求DTO
 * @author MilkTea Team
 */
@Data
public class OrderCreateRequest {
    
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    private Long userCouponId;
    @NotEmpty(message = "订单项不能为空")
    @Valid
    private List<OrderItemRequest> items;
    @Size(max = 500, message = "备注不能超过500字")
    private String remark;
    private Integer payType;
    
    @Data
    public static class OrderItemRequest {
        @NotNull(message = "商品ID不能为空")
        private Long productId;
        @NotNull(message = "数量不能为空")
        @Positive(message = "数量必须大于0")
        private Integer quantity;
        @Min(value = 0, message = "甜度参数无效") @Max(value = 4, message = "甜度参数无效")
        private Integer sweetness;
        @Min(value = 0, message = "温度参数无效") @Max(value = 3, message = "温度参数无效")
        private Integer temperature;
        private String toppings;
    }
}
