package com.milktea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.milktea.common.PageResult;
import com.milktea.common.Result;
import com.milktea.dto.OrderCreateRequest;
import com.milktea.entity.Order;
import com.milktea.entity.OrderItem;
import com.milktea.entity.Product;
import com.milktea.exception.BusinessException;
import com.milktea.mapper.OrderItemMapper;
import com.milktea.mapper.OrderMapper;
import com.milktea.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单服务类
 * @author MilkTea Team
 */
@Slf4j
@Service
public class OrderService {
    
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final com.milktea.mapper.UserMapper userMapper;
    
    public OrderService(OrderMapper orderMapper, OrderItemMapper orderItemMapper, ProductMapper productMapper, com.milktea.mapper.UserMapper userMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.productMapper = productMapper;
        this.userMapper = userMapper;
    }
    
    /**
     * 创建订单
     */
    @Transactional
    public Result<Map<String, Object>> createOrder(OrderCreateRequest request) {
        try {
            log.info("开始创建订单，用户ID: {}", request.getUserId());
            
            // 生成订单号
            String orderNo = generateOrderNo();
            log.info("生成订单号: {}", orderNo);
            
            // 计算订单总金额
            BigDecimal totalAmount = BigDecimal.ZERO;
            
            // 获取用户信息
            com.milktea.entity.User user = userMapper.selectById(request.getUserId());
            if (user == null) {
                log.error("用户不存在，用户ID: {}", request.getUserId());
                throw new BusinessException("用户不存在");
            }
            log.info("获取用户信息成功: {}", user.getUsername());
            
            // 创建订单
            Order order = new Order();
            order.setOrderNo(orderNo);
            order.setUserId(request.getUserId());
            order.setUsername(user.getUsername());
            order.setStatus(0); // 待支付
            order.setPayType(request.getPayType());
            order.setRemark(request.getRemark());
            order.setDiscountAmount(BigDecimal.ZERO);
            order.setTotalAmount(BigDecimal.ZERO); // 先设置为0，后续更新
            order.setPayAmount(BigDecimal.ZERO);
            order.setActualAmount(BigDecimal.ZERO);
            
            log.info("准备插入订单");
            orderMapper.insert(order);
            log.info("订单插入成功，订单ID: {}", order.getId());
        
            // 创建订单项
            log.info("开始创建订单项，商品数量: {}", request.getItems().size());
            for (OrderCreateRequest.OrderItemRequest itemRequest : request.getItems()) {
                log.info("处理商品ID: {}", itemRequest.getProductId());
                
                Product product = productMapper.selectById(itemRequest.getProductId());
                if (product == null || product.getStatus() == 0) {
                    log.error("商品不存在或已下架，商品ID: {}", itemRequest.getProductId());
                    throw new BusinessException("商品不存在或已下架");
                }
                
                if (product.getStock() < itemRequest.getQuantity()) {
                    log.error("商品库存不足，商品ID: {}, 库存: {}, 需要: {}", 
                        itemRequest.getProductId(), product.getStock(), itemRequest.getQuantity());
                    throw new BusinessException("商品库存不足");
                }
                
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(order.getId());
                orderItem.setProductId(product.getId());
                orderItem.setProductName(product.getName());
                orderItem.setProductImage(product.getImage());
                orderItem.setPrice(product.getPrice()); // 保存商品基础价格
                orderItem.setQuantity(itemRequest.getQuantity());
                orderItem.setSweetness(itemRequest.getSweetness());
                orderItem.setTemperature(itemRequest.getTemperature());
                orderItem.setToppings(itemRequest.getToppings());
                
                log.info("准备插入订单项");
                orderItemMapper.insert(orderItem);
                log.info("订单项插入成功");
                
                // 计算该商品的总金额（商品价格 × 数量）
                BigDecimal itemAmount = product.getPrice().multiply(new BigDecimal(itemRequest.getQuantity()));
                
                // 计算加料价格
                if (itemRequest.getToppings() != null && !itemRequest.getToppings().isEmpty()) {
                    String[] toppings = itemRequest.getToppings().split(",");
                    int toppingCount = toppings.length;
                    BigDecimal toppingPrice = new BigDecimal("3.00"); // 每个加料3元
                    BigDecimal toppingAmount = toppingPrice.multiply(new BigDecimal(toppingCount)).multiply(new BigDecimal(itemRequest.getQuantity()));
                    itemAmount = itemAmount.add(toppingAmount);
                    log.info("商品加料数量: {}, 加料总价: {}", toppingCount, toppingAmount);
                }
                
                totalAmount = totalAmount.add(itemAmount);
                log.info("商品小计: {}, 累计金额: {}", itemAmount, totalAmount);
                
                // 减库存
                product.setStock(product.getStock() - itemRequest.getQuantity());
                productMapper.updateById(product);
                log.info("库存更新成功");
            }
        
            // 更新订单总金额
            log.info("更新订单总金额: {}", totalAmount);
            order.setTotalAmount(totalAmount);
            order.setPayAmount(totalAmount);
            order.setActualAmount(totalAmount); // 实付金额等于应付金额
            orderMapper.updateById(order);
            log.info("订单更新成功");
            
            Map<String, Object> result = new HashMap<>();
            result.put("orderId", order.getId());
            result.put("orderNo", orderNo);
            result.put("totalAmount", totalAmount);
            
            log.info("订单创建完成，订单号: {}", orderNo);
            return Result.success("订单创建成功", result);
            
        } catch (BusinessException e) {
            log.error("业务异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("创建订单失败", e);
            throw new BusinessException("创建订单失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户订单列表
     */
    public Result<PageResult<Order>> getUserOrders(Long userId, Integer page, Integer size, Integer status) {
        log.info("查询用户订单列表，userId: {}, page: {}, size: {}, status: {}", userId, page, size, status);
        
        Page<Order> pageObj = new Page<>(page, size);
        
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId);
        
        if (status != null) {
            queryWrapper.eq(Order::getStatus, status);
        }
        
        queryWrapper.orderByDesc(Order::getCreateTime);
        
        Page<Order> result = orderMapper.selectPage(pageObj, queryWrapper);
        
        log.info("查询结果 - 记录数: {}, 总数: {}, 当前页: {}, 每页大小: {}", 
            result.getRecords().size(), result.getTotal(), result.getCurrent(), result.getSize());
        
        PageResult<Order> pageResult = new PageResult<>(
            result.getRecords(),
            result.getTotal(),
            result.getCurrent(),
            result.getSize()
        );
        
        log.info("返回PageResult - total: {}", pageResult.getTotal());
        
        return Result.success("查询成功", pageResult);
    }
    
    /**
     * 获取订单详情
     */
    public Result<Map<String, Object>> getOrderDetail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        LambdaQueryWrapper<OrderItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> orderItems = orderItemMapper.selectList(queryWrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("items", orderItems);
        
        return Result.success("获取成功", result);
    }
    
    /**
     * 取消订单
     */
    @Transactional
    public Result<String> cancelOrder(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        
        if (order.getStatus() != 0 && order.getStatus() != 1) {
            throw new BusinessException("订单状态不允许取消");
        }
        
        // 恢复库存
        LambdaQueryWrapper<OrderItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> orderItems = orderItemMapper.selectList(queryWrapper);
        
        for (OrderItem item : orderItems) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productMapper.updateById(product);
            }
        }
        
        // 更新订单状态
        order.setStatus(5); // 已取消
        order.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        return Result.success("订单取消成功");
    }
    
    /**
     * 申请退款
     */
    public Result<String> refundOrder(Long orderId, Long userId, String reason) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        
        if (order.getStatus() != 4) {
            throw new BusinessException("只有已完成的订单才能申请退款");
        }
        
        order.setStatus(6); // 申请退款
        order.setRefundReason(reason);
        orderMapper.updateById(order);
        
        return Result.success("退款申请提交成功");
    }
    
    /**
     * 确认收货
     */
    @Transactional
    public Result<String> confirmOrder(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        
        if (order.getStatus() != 3) {
            throw new BusinessException("只有待取餐的订单才能确认收货");
        }
        
        // 更新订单状态为已完成
        order.setStatus(4);
        order.setFinishTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        log.info("订单确认收货成功: orderId={}, userId={}", orderId, userId);
        return Result.success("确认收货成功");
    }
    
    /**
     * 订单评价
     */
    @Transactional
    public Result<String> evaluateOrder(Long orderId, Long userId, Map<String, Object> evaluateData) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        
        if (order.getStatus() != 4) {
            throw new BusinessException("只有已完成的订单才能评价");
        }
        
        // 这里可以将评价信息保存到评价表
        // 简化实现：只记录日志
        Integer rating = (Integer) evaluateData.get("rating");
        String comment = (String) evaluateData.get("comment");
        
        log.info("订单评价: orderId={}, userId={}, rating={}, comment={}", 
                orderId, userId, rating, comment);
        
        return Result.success("评价成功");
    }
    
    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.valueOf((int) (Math.random() * 1000));
        return "MT" + timestamp + String.format("%03d", Integer.parseInt(random));
    }
}
