package com.milktea.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.milktea.common.Result;
import com.milktea.entity.Staff;
import com.milktea.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理端员工管理控制器
 */
@RestController
@RequestMapping("/admin/staff")
public class AdminStaffController {

    @Autowired
    private StaffService staffService;

    /**
     * 获取员工统计信息
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        try {
            Map<String, Object> stats = staffService.getStaffStats();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取员工统计失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询员工列表
     */
    @GetMapping("/list")
    public Result<IPage<Staff>> getList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status) {
        try {
            IPage<Staff> pageData = staffService.getStaffList(page, size, keyword, role, status);
            return Result.success(pageData);
        } catch (Exception e) {
            return Result.error("查询员工列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取员工详情
     */
    @GetMapping("/{id}")
    public Result<Staff> getDetail(@PathVariable Long id) {
        try {
            Staff staff = staffService.getStaffById(id);
            if (staff == null) {
                return Result.error("员工不存在");
            }
            // 不返回密码
            staff.setPassword(null);
            return Result.success(staff);
        } catch (Exception e) {
            return Result.error("获取员工详情失败：" + e.getMessage());
        }
    }

    /**
     * 添加员工
     */
    @PostMapping
    public Result<String> add(@RequestBody Staff staff) {
        try {
            boolean success = staffService.addStaff(staff);
            if (success) {
                return Result.success("员工添加成功");
            } else {
                return Result.error("员工添加失败");
            }
        } catch (Exception e) {
            return Result.error("员工添加失败：" + e.getMessage());
        }
    }

    /**
     * 更新员工信息
     */
    @PutMapping
    public Result<String> update(@RequestBody Staff staff) {
        try {
            boolean success = staffService.updateStaff(staff);
            if (success) {
                return Result.success("员工信息更新成功");
            } else {
                return Result.error("员工信息更新失败");
            }
        } catch (Exception e) {
            return Result.error("员工信息更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除员工
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        try {
            boolean success = staffService.deleteStaff(id);
            if (success) {
                return Result.success("员工删除成功");
            } else {
                return Result.error("员工删除失败");
            }
        } catch (Exception e) {
            return Result.error("员工删除失败：" + e.getMessage());
        }
    }

    /**
     * 切换员工状态
     */
    @PutMapping("/{id}/toggle-status")
    public Result<String> toggleStatus(@PathVariable Long id) {
        try {
            boolean success = staffService.toggleStatus(id);
            if (success) {
                return Result.success("状态切换成功");
            } else {
                return Result.error("状态切换失败");
            }
        } catch (Exception e) {
            return Result.error("状态切换失败：" + e.getMessage());
        }
    }

    /**
     * 重置员工密码
     */
    @PutMapping("/{id}/reset-password")
    public Result<String> resetPassword(@PathVariable Long id) {
        try {
            boolean success = staffService.resetPassword(id);
            if (success) {
                return Result.success("密码重置成功，新密码为：123456");
            } else {
                return Result.error("密码重置失败");
            }
        } catch (Exception e) {
            return Result.error("密码重置失败：" + e.getMessage());
        }
    }

    /**
     * 更新员工权限
     */
    @PutMapping("/{id}/permissions")
    public Result<String> updatePermissions(
            @PathVariable Long id,
            @RequestBody Map<String, String> params) {
        try {
            String permissions = params.get("permissions");
            boolean success = staffService.updatePermissions(id, permissions);
            if (success) {
                return Result.success("权限更新成功");
            } else {
                return Result.error("权限更新失败");
            }
        } catch (Exception e) {
            return Result.error("权限更新失败：" + e.getMessage());
        }
    }

    /**
     * 获取角色默认权限
     */
    @GetMapping("/role-permissions")
    public Result<Map<String, String[]>> getRolePermissions() {
        try {
            Map<String, String[]> permissions = StaffService.getRoleDefaultPermissions();
            return Result.success(permissions);
        } catch (Exception e) {
            return Result.error("获取角色权限失败：" + e.getMessage());
        }
    }

    /**
     * 获取员工操作摘要
     */
    @GetMapping("/{id}/operation-summary")
    public Result<Map<String, Object>> getOperationSummary(@PathVariable Long id) {
        try {
            Map<String, Object> summary = staffService.getStaffOperationSummary(id);
            return Result.success(summary);
        } catch (Exception e) {
            return Result.error("获取操作摘要失败：" + e.getMessage());
        }
    }
}
