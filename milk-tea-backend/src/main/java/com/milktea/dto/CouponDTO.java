package com.milktea.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券DTO（包含用户领取状态）
 * @author MilkTea Team
 */
@Data
public class CouponDTO {
    
    private Long id;
    
    private String name;
    
    private Integer type;
    
    private BigDecimal discount;
    
    private BigDecimal minAmount;
    
    private Integer totalCount;
    
    private Integer receivedCount;
    
    private LocalDateTime validStart;
    
    private LocalDateTime validEnd;
    
    private Integer status;
    
    private LocalDateTime createTime;
    
    // 用户是否已领取
    private Boolean isReceived;
}
