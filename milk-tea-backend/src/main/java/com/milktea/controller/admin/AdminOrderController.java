package com.milktea.controller.admin;

import com.milktea.common.PageResult;
import com.milktea.common.Result;
import com.milktea.entity.Order;
import com.milktea.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理端订单控制器
 * @author MilkTea Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {
    
    private final AdminService adminService;
    
    public AdminOrderController(AdminService adminService) {
        this.adminService = adminService;
    }
    
    /**
     * 分页查询订单
     */
    @GetMapping("/page")
    public Result<PageResult<Order>> getOrderPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return adminService.getOrderPage(page, size, status, orderNo, startDate, endDate);
    }
    
    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getOrderDetail(@PathVariable Long id) {
        return adminService.getOrderDetail(id);
    }
    
    /**
     * 更新订单状态
     */
    @PutMapping("/{id}/status")
    public Result<String> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        Integer status = request.get("status");
        return adminService.updateOrderStatus(id, status);
    }
    
    /**
     * 处理退款
     */
    @PutMapping("/{id}/refund")
    public Result<String> handleRefund(@PathVariable Long id, @RequestBody Map<String, Boolean> request) {
        Boolean approve = request.get("approve");
        return adminService.handleRefund(id, approve);
    }
    
    /**
     * 获取订单统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getOrderStatistics() {
        return adminService.getOrderStatistics();
    }
    
    /**
     * 批量接单
     */
    @PutMapping("/batch/accept")
    public Result<String> batchAcceptOrders(@RequestBody Map<String, List<Long>> request) {
        List<Long> orderIds = request.get("orderIds");
        return adminService.batchAcceptOrders(orderIds);
    }
    
    /**
     * 批量完成订单
     */
    @PutMapping("/batch/complete")
    public Result<String> batchCompleteOrders(@RequestBody Map<String, List<Long>> request) {
        List<Long> orderIds = request.get("orderIds");
        return adminService.batchCompleteOrders(orderIds);
    }
    
    /**
     * 获取待处理订单数量
     */
    @GetMapping("/pending/count")
    public Result<Map<String, Object>> getPendingOrderCount() {
        return adminService.getPendingOrderCount();
    }
    
    /**
     * 获取今日订单概览
     */
    @GetMapping("/today/overview")
    public Result<Map<String, Object>> getTodayOrderOverview() {
        return adminService.getTodayOrderOverview();
    }
    
    /**
     * 订单备注更新
     */
    @PutMapping("/{id}/remark")
    public Result<String> updateOrderRemark(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String remark = request.get("remark");
        return adminService.updateOrderRemark(id, remark);
    }
    
    /**
     * 获取订单时间线
     */
    @GetMapping("/{id}/timeline")
    public Result<List<Map<String, Object>>> getOrderTimeline(@PathVariable Long id) {
        return adminService.getOrderTimeline(id);
    }
    
    /**
     * 订单催单处理
     */
    @PutMapping("/{id}/urgent")
    public Result<String> markOrderUrgent(@PathVariable Long id, @RequestBody Map<String, Boolean> request) {
        Boolean urgent = request.get("urgent");
        return adminService.markOrderUrgent(id, urgent);
    }
    
    /**
     * 申请退款
     */
    @PostMapping("/{id}/refund-request")
    public Result<String> requestRefund(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String reason = request.get("reason");
        return adminService.requestRefund(id, reason);
    }
    
    /**
     * 获取退款申请列表
     */
    @GetMapping("/refund/requests")
    public Result<PageResult<Map<String, Object>>> getRefundRequests(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        return adminService.getRefundRequests(page, size, status);
    }
    
    /**
     * 处理退款申请
     */
    @PutMapping("/refund/{id}/process")
    public Result<String> processRefund(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Boolean approve = (Boolean) request.get("approve");
        String rejectReason = (String) request.get("rejectReason");
        return adminService.processRefund(id, approve, rejectReason);
    }
    
    /**
     * 获取投诉列表
     */
    @GetMapping("/complaints")
    public Result<PageResult<Map<String, Object>>> getComplaints(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        return adminService.getComplaints(page, size, status);
    }
    
    /**
     * 处理投诉
     */
    @PutMapping("/complaint/{id}/process")
    public Result<String> processComplaint(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        String response = (String) request.get("response");
        Integer status = (Integer) request.get("status");
        return adminService.processComplaint(id, response, status);
    }
    
    /**
     * 获取售后统计
     */
    @GetMapping("/aftersale/statistics")
    public Result<Map<String, Object>> getAftersaleStatistics() {
        return adminService.getAftersaleStatistics();
    }
    
    /**
     * 导出订单数据
     */
    @GetMapping("/export")
    public Result<String> exportOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return adminService.exportOrders(status, orderNo, startDate, endDate);
    }
}
