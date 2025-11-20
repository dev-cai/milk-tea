package com.milktea.controller;

import com.milktea.common.PageResult;
import com.milktea.common.Result;
import com.milktea.dto.OrderCreateRequest;
import com.milktea.entity.Order;
import com.milktea.service.OrderService;
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
            @RequestParam(required = false) Integer status) {
        return orderService.getUserOrders(userId, page, size, status);
    }
    
    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getOrderDetail(@PathVariable Long id) {
        return orderService.getOrderDetail(id);
    }
    
    /**
     * 取消订单
     */
    @PutMapping("/{id}/cancel")
    public Result<String> cancelOrder(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        return orderService.cancelOrder(id, userId);
    }
    
    /**
     * 申请退款
     */
    @PutMapping("/{id}/refund")
    public Result<String> refundOrder(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        String reason = request.get("reason").toString();
        return orderService.refundOrder(id, userId, reason);
    }
}
