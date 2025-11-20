package com.milktea.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 投诉实体类
 * @author MilkTea Team
 */
@Data
@TableName("complaint")
public class Complaint {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private Long orderId;
    
    private String orderNo;
    
    private Long userId;
    
    private String customerName;
    
    private Integer complaintType; // 1:商品质量 2:服务态度 3:配送问题 4:其他
    
    private String content;
    
    private String images;
    
    private Integer status; // 0:待处理 1:处理中 2:已解决 3:已关闭
    
    private String response;
    
    private LocalDateTime processTime;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
