package com.milktea.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 门店实体类
 */
@Data
@TableName("store")
public class Store {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 门店名称
     */
    private String name;
    
    /**
     * 地址
     */
    private String address;
    
    /**
     * 联系电话
     */
    private String phone;
    
    /**
     * 店长姓名
     */
    private String manager;
    
    /**
     * 店长电话
     */
    private String managerPhone;
    
    /**
     * 营业时间（JSON格式）
     */
    private String businessHours;
    
    /**
     * 纬度
     */
    private BigDecimal latitude;
    
    /**
     * 经度
     */
    private BigDecimal longitude;
    
    /**
     * 门店面积（平方米）
     */
    private BigDecimal area;
    
    /**
     * 员工数量
     */
    private Integer staffCount;
    
    /**
     * 门店描述
     */
    private String description;
    
    /**
     * 门店图片（JSON格式）
     */
    private String images;
    
    /**
     * 状态：0-关闭，1-营业
     */
    private Integer status;
    
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
