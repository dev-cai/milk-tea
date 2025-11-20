package com.milktea.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 打印设备实体类
 * @author MilkTea Team
 */
@Data
@TableName("print_device")
public class PrintDevice {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String type; // thermal:热敏 dot:针式 laser:激光
    
    private String connection; // network:网络 usb:USB bluetooth:蓝牙
    
    private String ip;
    
    private Integer port;
    
    private String path; // USB设备路径
    
    private String mac; // 蓝牙MAC地址
    
    private Integer status; // 0:离线 1:在线
    
    private String description;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
