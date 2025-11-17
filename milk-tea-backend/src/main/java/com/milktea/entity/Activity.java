package com.milktea.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 营销活动实体类
 */
@Data
@TableName("activity")
public class Activity {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 活动名称
     */
    private String name;
    
    /**
     * 活动类型：1-满减，2-折扣，3-买一送一，4-限时特惠
     */
    private Integer type;
    
    /**
     * 活动描述
     */
    private String description;
    
    /**
     * 活动规则（JSON格式）
     */
    private String rules;
    
    /**
     * 折扣金额或折扣率
     */
    private BigDecimal discount;
    
    /**
     * 最低消费金额
     */
    private BigDecimal minAmount;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
    
    /**
     * 参与人数
     */
    private Integer participantCount;
    
    /**
     * 删除标识：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
    
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
