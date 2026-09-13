package com.milktea.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * @author MilkTea Team
 */
@Data
@TableName("orders")
public class Order {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String orderNo;
    
    private Long userId;
    
    private BigDecimal totalAmount;
    
    private BigDecimal discountAmount;
    
    private BigDecimal payAmount;
    
    private BigDecimal actualAmount;
    
    private String username;
    
    private Integer status; // 0:待支付 1:待制作 2:制作中 3:待取餐 4:已完成 5:已取消 6:申请退款 7:已退款
    
    private Integer payType; // 1:微信支付（开发测试）
    
    private String remark;
    
    private String refundReason;
    
    private LocalDateTime payTime;
    
    private LocalDateTime finishTime;
    
    private LocalDateTime cancelTime;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
