package com.milktea.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分历史实体类
 * @author MilkTea Team
 */
@Data
@TableName("points_history")
public class PointsHistory {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Integer points;
    
    private Integer type; // 1-购物，2-签到，3-邀请，4-评价，5-生日，6-兑换，7-过期
    
    private Long orderId;
    
    private String description;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
