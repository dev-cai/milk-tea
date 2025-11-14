package com.milktea.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品配方实体类
 * @author MilkTea Team
 */
@Data
@TableName("recipe")
public class Recipe {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private Long productId;
    
    private String ingredientName;
    
    private BigDecimal quantity;
    
    private String unit;
    
    private BigDecimal unitCost;
    
    private BigDecimal totalCost;
    
    private String description;
    
    private Integer sort;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
