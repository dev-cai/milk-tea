package com.milktea.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户优惠券DTO
 * @author MilkTea Team
 */
@Data
public class UserCouponDTO {
    
    private Long id; // 用户优惠券ID
    
    private Long userId;
    
    private Long couponId;
    
    private Integer status; // 0:未使用 1:已使用 2:已过期
    
    private Long orderId;
    
    private LocalDateTime useTime;
    
    private LocalDateTime createTime;
    
    // 优惠券详细信息
    private String name;
    
    private String description;
    
    private Integer type; // 1:满减券 2:折扣券 3:兑换券
    
    private BigDecimal discount;
    
    private BigDecimal minAmount;
    
    private LocalDateTime startTime; // 有效期开始
    
    private LocalDateTime endTime; // 有效期结束
}
