package com.milktea.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款申请实体类
 */
@Data
@TableName("refund_request")
public class RefundRequest {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 客户姓名
     */
    private String customerName;
    
    /**
     * 退款金额
     */
    private BigDecimal refundAmount;
    
    /**
     * 退款原因
     */
    private String reason;
    
    /**
     * 状态：0-待处理 1-已同意 2-已拒绝
     */
    private Integer status;
    
    /**
     * 拒绝原因
     */
    private String rejectReason;
    
    /**
     * 处理时间
     */
    private LocalDateTime processTime;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
