package com.milktea.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单项实体类
 * @author MilkTea Team
 */
@Data
@TableName("order_item")
public class OrderItem {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private Long orderId;
    
    private Long productId;
    
    private String productName;
    
    private String productImage;
    
    private BigDecimal price;
    
    private Integer quantity;
    
    private Integer sweetness; // 0:无糖 1:三分糖 2:五分糖 3:七分糖 4:正常糖
    
    private Integer temperature; // 0:去冰 1:少冰 2:正常冰 3:热饮
    
    private String toppings; // 加料，逗号分隔
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
