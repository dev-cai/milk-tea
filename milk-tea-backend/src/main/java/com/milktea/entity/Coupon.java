package com.milktea.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券实体类
 * @author MilkTea Team
 */
@Data
@TableName("coupon")
public class Coupon {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private Integer type; // 1:满减券 2:折扣券 3:兑换券
    
    private BigDecimal discount; // 折扣金额或折扣率
    
    private BigDecimal minAmount; // 最低消费金额
    
    private Integer totalCount; // 总发行数量
    
    private Integer receivedCount; // 已领取数量
    
    private LocalDateTime validStart; // 有效期开始时间
    
    private LocalDateTime validEnd; // 有效期结束时间
    
    private Integer status; // 0:禁用 1:启用
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
