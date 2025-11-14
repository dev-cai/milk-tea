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
    
    public OrderService(OrderMapper orderMapper, OrderItemMapper orderItemMapper, ProductMapper productMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.productMapper = productMapper;
    }
    
    /**
     * 创建订单
     */
    @Transactional
    public Result<Map<String, Object>> createOrder(OrderCreateRequest request) {
        // 生成订单号
        String orderNo = generateOrderNo();
        
        // 计算订单总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        // 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(request.getUserId());
        order.setStatus(0); // 待支付
        order.setPayType(request.getPayType());
        order.setRemark(request.getRemark());
        order.setDiscountAmount(BigDecimal.ZERO);
        
        orderMapper.insert(order);
        
        // 创建订单项
        for (OrderCreateRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productMapper.selectById(itemRequest.getProductId());
            if (product == null || product.getStatus() == 0) {
                throw new BusinessException("商品不存在或已下架");
            }
            
            if (product.getStock() < itemRequest.getQuantity()) {
                throw new BusinessException("商品库存不足");
            }
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getImage());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setSweetness(itemRequest.getSweetness());
            orderItem.setTemperature(itemRequest.getTemperature());
            orderItem.setToppings(itemRequest.getToppings());
            
            orderItemMapper.insert(orderItem);
            
            // 计算金额
            BigDecimal itemAmount = product.getPrice().multiply(new BigDecimal(itemRequest.getQuantity()));
            totalAmount = totalAmount.add(itemAmount);
            
            // 减库存
            product.setStock(product.getStock() - itemRequest.getQuantity());
            productMapper.updateById(product);
        }
        
        // 更新订单总金额
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        orderMapper.updateById(order);
        
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", order.getId());
        result.put("orderNo", orderNo);
        result.put("totalAmount", totalAmount);
        
        return Result.success("订单创建成功", result);
    }
    
    /**
     * 获取用户订单列表
     */
    public Result<PageResult<Order>> getUserOrders(Long userId, Integer page, Integer size, Integer status) {
        Page<Order> pageObj = new Page<>(page, size);
        
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId);
        
        if (status != null) {
            queryWrapper.eq(Order::getStatus, status);
        }
        
        queryWrapper.orderByDesc(Order::getCreateTime);
        
        Page<Order> result = orderMapper.selectPage(pageObj, queryWrapper);
        
        PageResult<Order> pageResult = new PageResult<>(
            result.getRecords(),
            result.getTotal(),
            result.getCurrent(),
            result.getSize()
        );
        
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
     * 生成订单号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.valueOf((int) (Math.random() * 1000));
        return "MT" + timestamp + String.format("%03d", Integer.parseInt(random));
    }
}
