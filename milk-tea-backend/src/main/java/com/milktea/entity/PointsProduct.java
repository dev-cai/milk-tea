package com.milktea.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 积分商品实体类
 */
@Data
@TableName("points_product")
public class PointsProduct {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 商品名称
     */
    private String name;
    
    /**
     * 商品描述
     */
    private String description;
    
    /**
     * 商品图片
     */
    private String image;
    
    /**
     * 所需积分
     */
    private Integer points;
    
    /**
     * 库存数量
     */
    private Integer stock;
    
    /**
     * 已兑换数量
     */
    private Integer exchanged;
    
    /**
     * 状态：0-下架，1-上架
     */
    private Integer status;
    
    /**
     * 排序
     */
    private Integer sort;
    
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
