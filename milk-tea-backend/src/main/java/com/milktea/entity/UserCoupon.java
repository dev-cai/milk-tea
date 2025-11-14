package com.milktea.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户优惠券实体类
 * @author MilkTea Team
 */
@Data
@TableName("user_coupon")
public class UserCoupon {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long couponId;
    
    private Integer status; // 0:未使用 1:已使用 2:已过期
    
    private Long orderId; // 使用的订单ID
    
    private LocalDateTime useTime; // 使用时间
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
