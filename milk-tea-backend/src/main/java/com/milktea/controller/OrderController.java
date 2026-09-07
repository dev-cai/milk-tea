package com.milktea.controller;

import com.milktea.common.PageResult;
import com.milktea.common.Result;
import com.milktea.dto.OrderCreateRequest;
import com.milktea.entity.Order;
import com.milktea.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 订单控制器
 * @author MilkTea Team
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    
    private final OrderService orderService;
    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Result<Map<String, Object>> createOrder(@RequestBody OrderCreateRequest request) {
        return orderService.createOrder(request);
    }
    
    /**
     * 获取用户订单列表
     */
    @GetMapping("/list")
    public Result<PageResult<Order>> getUserOrders(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            HttpServletRequest servletRequest) {
        return orderService.getUserOrders(verifyUser(userId, servletRequest), page, size, status);
    }
    
    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getOrderDetail(@PathVariable Long id, HttpServletRequest servletRequest) {
        return orderService.getOrderDetail(id, verifyUser(null, servletRequest));
    }
    
    /**
     * 取消订单
     */
    @PutMapping("/{id}/cancel")
    public Result<String> cancelOrder(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> request,
                                      HttpServletRequest servletRequest) {
        Long userId = verifyUser(request == null ? null : toLong(request.get("userId")), servletRequest);
        return orderService.cancelOrder(id, userId);
    }
    
    /**
     * 申请退款
     */
    @PutMapping("/{id}/refund")
    public Result<String> refundOrder(@PathVariable Long id, @RequestBody Map<String, Object> request,
                                      HttpServletRequest servletRequest) {
        Long userId = verifyUser(toLong(request.get("userId")), servletRequest);
        String reason = request.get("reason") == null ? "未填写原因" : request.get("reason").toString();
        return orderService.refundOrder(id, userId, reason);
    }
    
    /**
     * 确认收货
     */
    @PutMapping("/{id}/confirm")
    public Result<String> confirmOrder(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> request,
                                       HttpServletRequest servletRequest) {
        Long userId = verifyUser(request == null ? null : toLong(request.get("userId")), servletRequest);
        return orderService.confirmOrder(id, userId);
    }
    
    /**
     * 订单评价
     */
    @PostMapping("/{id}/evaluate")
    public Result<String> evaluateOrder(@PathVariable Long id, @RequestBody Map<String, Object> request,
                                        HttpServletRequest servletRequest) {
        Long userId = verifyUser(toLong(request.get("userId")), servletRequest);
        return orderService.evaluateOrder(id, userId, request);
    }
    
    /**
     * 提交投诉
     */
    @PostMapping("/complaint")
    public Result<String> submitComplaint(@RequestBody Map<String, Object> request, HttpServletRequest servletRequest) {
        request.put("userId", verifyUser(toLong(request.get("userId")), servletRequest));
        return orderService.submitComplaint(request);
    }
    
    /**
     * 获取用户投诉列表
     */
    @GetMapping("/complaints")
    public Result<PageResult<Map<String, Object>>> getUserComplaints(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest servletRequest) {
        return orderService.getUserComplaints(verifyUser(userId, servletRequest), page, size);
    }

    private Long verifyUser(Long suppliedUserId, HttpServletRequest request) {
        Object authenticatedUserId = request.getAttribute("authenticatedUserId");
        if (authenticatedUserId == null) {
            throw new IllegalStateException("未获取到登录用户信息");
        }
        Long currentUserId = Long.valueOf(authenticatedUserId.toString());
        if (suppliedUserId != null && !currentUserId.equals(suppliedUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("无权操作其他用户数据");
        }
        return currentUserId;
    }

    private Long toLong(Object value) {
        return value == null ? null : Long.valueOf(value.toString());
    }
}
