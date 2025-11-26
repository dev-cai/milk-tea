package com.milktea.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 邀请记录实体类
 * @author MilkTea Team
 */
@Data
@TableName("invite_record")
public class InviteRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long inviterId;
    
    private Long inviteeId;
    
    private Integer status;
    
    private Integer points;
    
    private LocalDateTime createTime;
    
    private LocalDateTime rewardTime;
}
