package com.milktea.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 * @author MilkTea Team
 */
@Data
@TableName("product")
public class Product {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private Long categoryId;
    
    private String description;
    
    private String image;
    
    private BigDecimal price;
    
    private BigDecimal memberPrice;
    
    private BigDecimal cost;
    
    private Integer stock;
    
    private Integer sales;
    
    private String nutrition;
    
    private Integer status;
    
    private Integer isRecommend;
    
    private Integer sort;
    
    @TableField(exist = false)
    private String categoryName; // 分类名称，非数据库字段
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
