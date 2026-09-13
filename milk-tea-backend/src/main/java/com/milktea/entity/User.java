package com.milktea.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户实体类 - Spring Boot 3
 * @author MilkTea Team
 */
@Data
@TableName("user")
public class User {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String password;
    
    private String nickname;
    
    private String avatar;
    
    private String phone;
    
    private String openid;
    
    private Integer gender;
    
    private String birthday;
    
    private Integer memberLevel;
    
    private String memberNo;
    
    private String inviteCode;
    
    private Long inviterId;
    
    private LocalDateTime inviteTime;
    
    private Integer points;
    
    private BigDecimal balance;
    
    private Integer userType;
    
    private Integer status;

    /** Incremented on password/status-sensitive changes to invalidate issued tokens. */
    private Integer tokenVersion;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    private LocalDateTime lastLoginTime;
}
