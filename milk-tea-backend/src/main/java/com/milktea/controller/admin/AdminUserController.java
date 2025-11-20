package com.milktea.controller.admin;

import com.milktea.common.PageResult;
import com.milktea.common.Result;
import com.milktea.entity.User;
import com.milktea.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理端用户控制器
 * @author MilkTea Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/user")
public class AdminUserController {
    
    private final AdminService adminService;
    
    public AdminUserController(AdminService adminService) {
        this.adminService = adminService;
    }
    
    /**
     * 分页查询用户
     */
    @GetMapping("/page")
    public Result<PageResult<User>> getUserPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer memberLevel) {
        return adminService.getUserPage(page, size, keyword, memberLevel);
    }
    
    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public Result<User> getUserDetail(@PathVariable Long id) {
        return adminService.getUserDetail(id);
    }
    
    /**
     * 更新用户状态
     */
    @PutMapping("/{id}/status")
    public Result<String> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        Integer status = request.get("status");
        return adminService.updateUserStatus(id, status);
    }
    
    /**
     * 调整用户会员等级
     */
    @PutMapping("/{id}/member-level")
    public Result<String> updateUserMemberLevel(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        Integer memberLevel = request.get("memberLevel");
        return adminService.updateUserMemberLevel(id, memberLevel);
    }
    
    /**
     * 调整用户积分
     */
    @PutMapping("/{id}/points")
    public Result<String> updateUserPoints(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Integer points = (Integer) request.get("points");
        String type = (String) request.get("type");
        return adminService.updateUserPoints(id, points, type);
    }
    
    /**
     * 调整用户余额
     */
    @PutMapping("/{id}/balance")
    public Result<String> updateUserBalance(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Double balance = (Double) request.get("balance");
        String type = (String) request.get("type");
        String reason = (String) request.get("reason");
        return adminService.updateUserBalance(id, balance, type, reason);
    }
    
    /**
     * 获取用户消费统计
     */
    @GetMapping("/{id}/statistics")
    public Result<Map<String, Object>> getUserStatistics(@PathVariable Long id) {
        return adminService.getUserStatistics(id);
    }
    
    /**
     * 获取用户订单历史
     */
    @GetMapping("/{id}/orders")
    public Result<PageResult<Map<String, Object>>> getUserOrders(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return adminService.getUserOrders(id, page, size);
    }
    
    /**
     * 批量更新用户状态
     */
    @PutMapping("/batch/status")
    public Result<String> batchUpdateUserStatus(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Long> userIds = (List<Long>) request.get("userIds");
        Integer status = (Integer) request.get("status");
        return adminService.batchUpdateUserStatus(userIds, status);
    }
    
    /**
     * 获取会员统计
     */
    @GetMapping("/member/statistics")
    public Result<Map<String, Object>> getMemberStatistics() {
        return adminService.getMemberStatistics();
    }
    
    /**
     * 获取用户增长趋势
     */
    @GetMapping("/growth/trend")
    public Result<Map<String, Object>> getUserGrowthTrend(@RequestParam(defaultValue = "30") Integer days) {
        return adminService.getUserGrowthTrend(days);
    }
    
    /**
     * 导出用户数据
     */
    @GetMapping("/export")
    public Result<String> exportUsers(@RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) Integer memberLevel) {
        return adminService.exportUsers(keyword, memberLevel);
    }
}
