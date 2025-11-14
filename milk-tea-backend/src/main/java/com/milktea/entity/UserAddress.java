package com.milktea.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户地址实体类
 * @author MilkTea Team
 */
@Data
@TableName("user_address")
public class UserAddress {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String name;
    
    private String phone;
    
    private String province;
    
    private String city;
    
    private String district;
    
    private String detail;
    
    private Integer tag; // 1:家 2:公司 3:学校 4:其他
    
    private Integer isDefault;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
