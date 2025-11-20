package com.milktea.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 营销活动实体类
 * @author MilkTea Team
 */
@Data
@TableName("marketing_activity")
public class MarketingActivity {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String type;
    
    private String description;
    
    private String rules;
    
    private String banner;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private Integer participants;
    
    private BigDecimal revenue;
    
    private Integer status;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
