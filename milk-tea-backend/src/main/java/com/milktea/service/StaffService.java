package com.milktea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.milktea.entity.Staff;
import com.milktea.mapper.StaffMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工服务类
 */
@Service
public class StaffService {

    @Autowired
    private StaffMapper staffMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 分页查询员工列表
     */
    public IPage<Staff> getStaffList(int page, int size, String keyword, String role, Integer status) {
        Page<Staff> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Staff> wrapper = new LambdaQueryWrapper<>();
        
        // 关键字搜索（姓名或手机号）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Staff::getName, keyword)
                    .or()
                    .like(Staff::getPhone, keyword)
                    .or()
                    .like(Staff::getEmployeeId, keyword));
        }
        
        // 角色筛选
        if (StringUtils.hasText(role)) {
            wrapper.eq(Staff::getRole, role);
        }
        
        // 状态筛选
        if (status != null) {
            wrapper.eq(Staff::getStatus, status);
        }
        
        wrapper.orderByDesc(Staff::getCreateTime);
        
        return staffMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 获取员工统计信息
     */
    public Map<String, Object> getStaffStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 总员工数
        Long total = staffMapper.selectCount(null);
        stats.put("total", total);
        
        // 在职员工数
        Long active = staffMapper.selectCount(new LambdaQueryWrapper<Staff>()
                .eq(Staff::getStatus, 1));
        stats.put("active", active);
        
        // 管理员数量（包括超级管理员和店长）
        Long managers = staffMapper.selectCount(new LambdaQueryWrapper<Staff>()
                .in(Staff::getRole, "super_admin", "manager")
                .eq(Staff::getStatus, 1));
        stats.put("managers", managers);
        
        // 当前在岗（最近24小时内登录过的在职员工）
        Long onDuty = staffMapper.selectCount(new LambdaQueryWrapper<Staff>()
                .eq(Staff::getStatus, 1)
                .ge(Staff::getLastLogin, LocalDateTime.now().minusHours(24)));
        stats.put("onDuty", onDuty);
        
        return stats;
    }

    /**
     * 根据ID获取员工详情
     */
    public Staff getStaffById(Long id) {
        return staffMapper.selectById(id);
    }

    /**
     * 添加员工
     */
    public boolean addStaff(Staff staff) {
        // 检查用户名是否已存在
        Long count = staffMapper.selectCount(new LambdaQueryWrapper<Staff>()
                .eq(Staff::getUsername, staff.getUsername()));
        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查工号是否已存在
        count = staffMapper.selectCount(new LambdaQueryWrapper<Staff>()
                .eq(Staff::getEmployeeId, staff.getEmployeeId()));
        if (count > 0) {
            throw new RuntimeException("工号已存在");
        }
        
        // 设置默认密码（BCrypt加密）
        if (!StringUtils.hasText(staff.getPassword())) {
            staff.setPassword(passwordEncoder.encode("123456"));
        } else {
            staff.setPassword(passwordEncoder.encode(staff.getPassword()));
        }
        
        // 设置默认状态
        if (staff.getStatus() == null) {
            staff.setStatus(1);
        }
        
        return staffMapper.insert(staff) > 0;
    }

    /**
     * 更新员工信息
     */
    public boolean updateStaff(Staff staff) {
        Staff existingStaff = staffMapper.selectById(staff.getId());
        if (existingStaff == null) {
            throw new RuntimeException("员工不存在");
        }
        
        // 如果修改了用户名，检查是否重复
        if (StringUtils.hasText(staff.getUsername()) 
                && !staff.getUsername().equals(existingStaff.getUsername())) {
            Long count = staffMapper.selectCount(new LambdaQueryWrapper<Staff>()
                    .eq(Staff::getUsername, staff.getUsername())
                    .ne(Staff::getId, staff.getId()));
            if (count > 0) {
                throw new RuntimeException("用户名已存在");
            }
        }
        
        // 如果修改了工号，检查是否重复
        if (StringUtils.hasText(staff.getEmployeeId()) 
                && !staff.getEmployeeId().equals(existingStaff.getEmployeeId())) {
            Long count = staffMapper.selectCount(new LambdaQueryWrapper<Staff>()
                    .eq(Staff::getEmployeeId, staff.getEmployeeId())
                    .ne(Staff::getId, staff.getId()));
            if (count > 0) {
                throw new RuntimeException("工号已存在");
            }
        }
        
        // 不允许通过此接口修改密码
        staff.setPassword(null);
        
        return staffMapper.updateById(staff) > 0;
    }

    /**
     * 删除员工（逻辑删除）
     */
    public boolean deleteStaff(Long id) {
        return staffMapper.deleteById(id) > 0;
    }

    /**
     * 切换员工状态（在职/离职）
     */
    public boolean toggleStatus(Long id) {
        Staff staff = staffMapper.selectById(id);
        if (staff == null) {
            throw new RuntimeException("员工不存在");
        }
        
        staff.setStatus(staff.getStatus() == 1 ? 0 : 1);
        return staffMapper.updateById(staff) > 0;
    }

    /**
     * 重置员工密码
     */
    public boolean resetPassword(Long id) {
        Staff staff = staffMapper.selectById(id);
        if (staff == null) {
            throw new RuntimeException("员工不存在");
        }
        
        // 重置为默认密码123456
        staff.setPassword(passwordEncoder.encode("123456"));
        return staffMapper.updateById(staff) > 0;
    }

    /**
     * 更新员工权限
     */
    public boolean updatePermissions(Long id, String permissions) {
        Staff staff = staffMapper.selectById(id);
        if (staff == null) {
            throw new RuntimeException("员工不存在");
        }
        
        staff.setPermissions(permissions);
        return staffMapper.updateById(staff) > 0;
    }

    /**
     * 更新最后登录时间
     */
    public void updateLastLogin(Long id) {
        Staff staff = new Staff();
        staff.setId(id);
        staff.setLastLogin(LocalDateTime.now());
        staffMapper.updateById(staff);
    }

    /**
     * 获取角色的默认权限
     */
    public static Map<String, String[]> getRoleDefaultPermissions() {
        Map<String, String[]> rolePermissions = new HashMap<>();
        
        // 超级管理员权限（全部权限）
        rolePermissions.put("super_admin", new String[]{
            "dashboard.view", "dashboard.export",
            "product.view", "product.create", "product.edit", "product.delete", "product.stock",
            "order.view", "order.process", "order.refund", "order.print",
            "user.view", "user.edit", "user.analysis",
            "marketing.view", "marketing.create", "marketing.edit", "marketing.delete",
            "system.staff", "system.config", "system.log"
        });
        
        // 店长权限
        rolePermissions.put("manager", new String[]{
            "dashboard.view", "dashboard.export",
            "product.view", "product.edit", "product.stock",
            "order.view", "order.process", "order.refund", "order.print",
            "user.view", "user.analysis",
            "marketing.view",
            "system.log"
        });
        
        // 收银员权限
        rolePermissions.put("cashier", new String[]{
            "dashboard.view",
            "product.view",
            "order.view", "order.process", "order.print",
            "user.view"
        });
        
        // 制作员权限
        rolePermissions.put("maker", new String[]{
            "product.view",
            "order.view", "order.process"
        });
        
        return rolePermissions;
    }

    /**
     * 获取员工操作日志（简化版，从员工表的最后登录时间等信息推断）
     */
    public Map<String, Object> getStaffOperationSummary(Long staffId) {
        Staff staff = staffMapper.selectById(staffId);
        if (staff == null) {
            throw new RuntimeException("员工不存在");
        }
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("staffId", staffId);
        summary.put("staffName", staff.getName());
        summary.put("lastLogin", staff.getLastLogin());
        summary.put("createTime", staff.getCreateTime());
        summary.put("updateTime", staff.getUpdateTime());
        
        // 简单统计
        summary.put("totalDays", java.time.temporal.ChronoUnit.DAYS.between(
            staff.getCreateTime().toLocalDate(), 
            LocalDateTime.now().toLocalDate()
        ));
        
        return summary;
    }
}
