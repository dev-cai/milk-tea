package com.milktea.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 打印模板实体类
 * @author MilkTea Team
 */
@Data
@TableName("print_template")
public class PrintTemplate {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String type; // order:订单小票 kitchen:厨房单 delivery:配送单
    
    private Integer width; // 纸张宽度(mm)
    
    private String content; // 模板内容
    
    private String header; // 页眉
    
    private String footer; // 页脚
    
    private Integer isDefault; // 0:否 1:是
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
