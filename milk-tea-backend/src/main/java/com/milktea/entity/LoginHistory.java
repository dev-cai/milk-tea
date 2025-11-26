package com.milktea.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录记录实体类
 * @author MilkTea Team
 */
@Data
@TableName("login_history")
public class LoginHistory {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String device;
    
    private String deviceType;
    
    private String ip;
    
    private String location;
    
    private LocalDateTime loginTime;
}
