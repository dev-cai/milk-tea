package com.milktea.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 打印记录实体类
 * @author MilkTea Team
 */
@Data
@TableName("print_record")
public class PrintRecord {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private Long orderId;
    
    private Long deviceId;
    
    private Long templateId;
    
    private Integer status; // 1:成功 2:失败 3:处理中
    
    private String errorMessage;
    
    private Integer copies; // 打印份数
    
    private Integer duration; // 打印耗时(秒)
    
    private Integer retryCount; // 重试次数
    
    private LocalDateTime printTime;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    // 非数据库字段 - 用于前端展示
    @TableField(exist = false)
    private String orderNo;
    
    @TableField(exist = false)
    private String deviceName;
    
    @TableField(exist = false)
    private String templateName;
    
    @TableField(exist = false)
    private String content; // 动态生成的打印内容
}
