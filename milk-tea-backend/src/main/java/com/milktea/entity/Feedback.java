package com.milktea.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户反馈实体类
 */
@Data
@TableName("feedback")
public class Feedback {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 反馈类型：1-产品问题 2-服务问题 3-配送问题 4-支付问题 5-功能建议 6-其他问题
     */
    private Integer type;
    
    /**
     * 反馈内容
     */
    private String content;
    
    /**
     * 图片地址，多张图片用逗号分隔
     */
    private String images;
    
    /**
     * 联系电话
     */
    private String contactPhone;
    
    /**
     * 联系邮箱
     */
    private String contactEmail;
    
    /**
     * 处理状态：0-待处理 1-处理中 2-已回复
     */
    private Integer status;
    
    /**
     * 客服回复内容
     */
    private String reply;
    
    /**
     * 回复时间
     */
    private LocalDateTime replyTime;
    
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
