package com.milktea.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款申请实体类
 * @author MilkTea Team
 */
@Data
@TableName("refund_request")
public class RefundRequest {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private Long orderId;
    
    private String orderNo;
    
    private Long userId;
    
    private String customerName;
    
    private BigDecimal refundAmount;
    
    private String reason;
    
    private Integer status; // 0:待处理 1:已同意 2:已拒绝
    
    private String rejectReason;
    
    private LocalDateTime processTime;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
