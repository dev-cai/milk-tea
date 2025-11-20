package com.milktea.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 员工实体类
 */
@Data
@TableName("staff")
public class Staff {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 工号
     */
    private String employeeId;
    
    /**
     * 角色：super_admin-超级管理员，manager-店长，cashier-收银员，maker-制作员
     */
    private String role;
    
    /**
     * 部门：management-管理部，front-前台部，kitchen-制作部，delivery-配送部
     */
    private String department;
    
    /**
     * 入职时间
     */
    private LocalDate hireDate;
    
    /**
     * 薪资
     */
    private BigDecimal salary;
    
    /**
     * 地址
     */
    private String address;
    
    /**
     * 权限（JSON格式）
     */
    private String permissions;
    
    /**
     * 状态：0-离职，1-在职
     */
    private Integer status;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLogin;
    
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
