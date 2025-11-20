package com.milktea.service;

import com.milktea.common.Result;
import com.milktea.entity.Order;
import com.milktea.entity.User;
import com.milktea.exception.BusinessException;
import com.milktea.mapper.OrderMapper;
import com.milktea.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final UserMapper userMapper;
    
    public PaymentService(OrderMapper orderMapper, UserMapper userMapper) {
        this.orderMapper = orderMapper;
        this.userMapper = userMapper;
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
            
            if (order.getStatus() != 0) {
                throw new BusinessException("订单状态不正确");
            }
            
            // 测试环境：直接模拟支付成功
            // 更新订单状态
            order.setStatus(1); // 待制作
            order.setPayTime(LocalDateTime.now());
            order.setPayAmount(amount);
            order.setActualAmount(amount);
            orderMapper.updateById(order);
            
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
    
    /**
     * 支付宝支付（测试环境）
     */
    @Transactional
    public Result<Map<String, Object>> alipay(Long orderId, String orderNo, BigDecimal amount, Long userId) {
        try {
            log.info("开始支付宝支付，订单ID: {}, 订单号: {}, 金额: {}, 用户ID: {}", orderId, orderNo, amount, userId);
            
            // 获取订单
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                throw new BusinessException("订单不存在");
            }
            
            if (order.getStatus() != 0) {
                throw new BusinessException("订单状态不正确");
            }
            
            // 测试环境：直接模拟支付成功
            // 更新订单状态
            order.setStatus(1); // 待制作
            order.setPayTime(LocalDateTime.now());
            order.setPayAmount(amount);
            order.setActualAmount(amount);
            orderMapper.updateById(order);
            
            log.info("支付宝支付成功，订单ID: {}", orderId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("orderId", orderId);
            result.put("orderNo", orderNo);
            result.put("payTime", LocalDateTime.now());
            
            return Result.success("支付成功", result);
            
        } catch (BusinessException e) {
            log.error("支付宝支付失败: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("支付宝支付异常", e);
            throw new BusinessException("支付失败: " + e.getMessage());
        }
    }
    
    /**
     * 余额支付
     */
    @Transactional
    public Result<Map<String, Object>> balancePay(Long orderId, String orderNo, BigDecimal amount, Long userId) {
        try {
            log.info("开始余额支付，订单ID: {}, 订单号: {}, 金额: {}, 用户ID: {}", orderId, orderNo, amount, userId);
            
            // 获取订单
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                throw new BusinessException("订单不存在");
            }
            
            if (order.getStatus() != 0) {
                throw new BusinessException("订单状态不正确");
            }
            
            // 获取用户
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            
            // 检查余额
            BigDecimal balance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
            if (balance.compareTo(amount) < 0) {
                throw new BusinessException("余额不足");
            }
            
            // 扣减余额
            BigDecimal newBalance = balance.subtract(amount);
            user.setBalance(newBalance);
            userMapper.updateById(user);
            
            log.info("用户余额扣减成功，用户ID: {}, 原余额: {}, 新余额: {}", userId, balance, newBalance);
            
            // 更新订单状态
            order.setStatus(1); // 待制作
            order.setPayTime(LocalDateTime.now());
            order.setPayAmount(amount);
            order.setActualAmount(amount);
            orderMapper.updateById(order);
            
            log.info("余额支付成功，订单ID: {}", orderId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("orderId", orderId);
            result.put("orderNo", orderNo);
            result.put("payTime", LocalDateTime.now());
            result.put("balance", newBalance);
            
            return Result.success("支付成功", result);
            
        } catch (BusinessException e) {
            log.error("余额支付失败: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("余额支付异常", e);
            throw new BusinessException("支付失败: " + e.getMessage());
        }
    }
}
