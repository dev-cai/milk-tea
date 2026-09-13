package com.milktea.service;

import com.milktea.common.Result;
import com.milktea.entity.Order;
import com.milktea.exception.BusinessException;
import com.milktea.mapper.OrderMapper;
import com.milktea.mapper.OrderItemMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.milktea.entity.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付服务类
 * @author MilkTea Team
 */
@Slf4j
@Service
public class PaymentService {
    
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final com.milktea.mapper.ProductMapper productMapper;
    private final StringRedisTemplate redisTemplate;
    
    public PaymentService(OrderMapper orderMapper, OrderItemMapper orderItemMapper,
                          com.milktea.mapper.ProductMapper productMapper, StringRedisTemplate redisTemplate) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.productMapper = productMapper;
        this.redisTemplate = redisTemplate;
    }
    
    /**
     * 微信支付（测试环境）
     */
    @Transactional
    public Result<Map<String, Object>> wxPay(Long orderId, String orderNo, BigDecimal amount, Long userId) {
        try {
            log.info("开始微信支付，订单ID: {}, 订单号: {}, 金额: {}, 用户ID: {}", orderId, orderNo, amount, userId);
            
            // 获取订单
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                throw new BusinessException("订单不存在");
            }
            
            verifyPaymentRequest(order, orderNo, amount, userId);
            markPaid(order, amount);
            
            log.info("微信支付成功，订单ID: {}", orderId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("orderId", orderId);
            result.put("orderNo", orderNo);
            result.put("payTime", LocalDateTime.now());
            
            return Result.success("支付成功", result);
            
        } catch (BusinessException e) {
            log.error("微信支付失败: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("微信支付异常", e);
            throw new BusinessException("支付失败: " + e.getMessage());
        }
    }
    
    private void verifyPaymentRequest(Order order, String orderNo, BigDecimal amount, Long userId) {
        if (!userId.equals(order.getUserId()) || (orderNo != null && !orderNo.equals(order.getOrderNo()))) {
            throw new BusinessException("无权支付此订单");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0 || amount.compareTo(order.getPayAmount()) != 0) {
            throw new BusinessException("支付金额与订单不一致");
        }
        if (order.getStatus() != 0) throw new BusinessException("订单已支付或状态不正确");
    }

    private void markPaid(Order order, BigDecimal amount) {
        OrderStateMachine.assertTransition(order.getStatus(), 1);
        if (orderMapper.markPaidIfPending(order.getId(), order.getUserId(), amount) != 1) {
            throw new BusinessException("订单已支付，请勿重复支付");
        }
        for (OrderItem item : orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()))) {
            productMapper.increaseSales(item.getProductId(), item.getQuantity());
        }
        try {
            java.util.Set<String> keys = redisTemplate.keys("recommend:user:" + order.getUserId() + ":*");
            if (keys != null && !keys.isEmpty()) redisTemplate.delete(keys);
        } catch (Exception cacheError) {
            log.debug("清理推荐缓存失败", cacheError);
        }
    }
}
