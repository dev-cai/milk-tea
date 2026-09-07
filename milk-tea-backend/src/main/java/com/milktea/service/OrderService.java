package com.milktea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.milktea.common.PageResult;
import com.milktea.common.Result;
import com.milktea.dto.OrderCreateRequest;
import com.milktea.entity.Order;
import com.milktea.entity.OrderItem;
import com.milktea.entity.Product;
import com.milktea.entity.RefundRequest;
import com.milktea.exception.BusinessException;
import com.milktea.mapper.OrderItemMapper;
import com.milktea.mapper.OrderMapper;
import com.milktea.mapper.ProductMapper;
import com.milktea.mapper.ReviewMapper;
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
    private final com.milktea.mapper.ComplaintMapper complaintMapper;
    private final com.milktea.mapper.RefundRequestMapper refundRequestMapper;
    private final ReviewMapper reviewMapper;
    
    public OrderService(OrderMapper orderMapper, OrderItemMapper orderItemMapper, ProductMapper productMapper, 
                       com.milktea.mapper.UserMapper userMapper,
                       com.milktea.mapper.ComplaintMapper complaintMapper,
                       com.milktea.mapper.RefundRequestMapper refundRequestMapper,
                       ReviewMapper reviewMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.productMapper = productMapper;
        this.userMapper = userMapper;
        this.complaintMapper = complaintMapper;
        this.refundRequestMapper = refundRequestMapper;
        this.reviewMapper = reviewMapper;
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
                
                // 原子扣减库存，避免并发下单导致超卖
                int updated = productMapper.decrementStock(product.getId(), itemRequest.getQuantity());
                if (updated != 1) {
                    throw new BusinessException("商品库存不足");
                }
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

    public Result<Map<String, Object>> getOrderDetail(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权查看此订单");
        }
        return getOrderDetail(orderId);
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
    @Transactional
    public Result<String> refundOrder(Long orderId, Long userId, String reason) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        
        // 允许已支付(1)、制作中(2)、待取餐(3)、已完成(4)的订单申请退款
        if (order.getStatus() < 1 || order.getStatus() > 4) {
            throw new BusinessException("当前订单状态不允许申请退款");
        }
        
        // 创建退款申请记录
        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setOrderId(order.getId());
        refundRequest.setOrderNo(order.getOrderNo());
        refundRequest.setUserId(userId);
        refundRequest.setCustomerName(order.getUsername());
        refundRequest.setRefundAmount(order.getPayAmount());
        refundRequest.setReason(reason);
        refundRequest.setStatus(0); // 待处理
        
        refundRequestMapper.insert(refundRequest);
        
        // 更新订单状态为退款中
        order.setStatus(6); // 申请退款
        order.setRefundReason(reason);
        orderMapper.updateById(order);
        
        log.info("退款申请成功: 订单ID={}, 退款金额={}", orderId, order.getPayAmount());
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
        
        Integer rating = evaluateData.get("rating") == null ? null : Integer.valueOf(evaluateData.get("rating").toString());
        if (rating == null || rating < 1 || rating > 5) {
            throw new BusinessException("评分必须为1-5分");
        }
        String comment = evaluateData.get("comment") == null
                ? (evaluateData.get("content") == null ? "" : evaluateData.get("content").toString())
                : evaluateData.get("comment").toString();
        if (comment.length() > 500) {
            throw new BusinessException("评价内容不能超过500字");
        }

        Long productId = evaluateData.get("productId") == null ? null
                : Long.valueOf(evaluateData.get("productId").toString());
        if (productId == null) {
            OrderItem firstItem = orderItemMapper.selectOne(new LambdaQueryWrapper<OrderItem>()
                    .eq(OrderItem::getOrderId, orderId).last("LIMIT 1"));
            if (firstItem == null) {
                throw new BusinessException("订单没有可评价的商品");
            }
            productId = firstItem.getProductId();
        }
        Long existing = reviewMapper.selectCount(new LambdaQueryWrapper<com.milktea.entity.Review>()
                .eq(com.milktea.entity.Review::getOrderId, orderId)
                .eq(com.milktea.entity.Review::getUserId, userId)
                .eq(com.milktea.entity.Review::getProductId, productId));
        if (existing > 0) {
            throw new BusinessException("该商品已经评价过了");
        }

        com.milktea.entity.Review review = new com.milktea.entity.Review();
        review.setOrderId(orderId);
        review.setUserId(userId);
        review.setProductId(productId);
        review.setRating(rating);
        review.setContent(comment);
        review.setImages(evaluateData.get("images") == null ? null : evaluateData.get("images").toString());
        review.setIsAnonymous(evaluateData.get("isAnonymous") == null ? 0
                : Integer.valueOf(evaluateData.get("isAnonymous").toString()));
        reviewMapper.insert(review);

        log.info("订单评价已保存: orderId={}, userId={}, productId={}, rating={}",
                orderId, userId, productId, rating);

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
    
    /**
     * 提交投诉
     */
    @Transactional
    public Result<String> submitComplaint(Map<String, Object> request) {
        try {
            Long orderId = Long.valueOf(request.get("orderId").toString());
            String orderNo = request.get("orderNo").toString();
            Long userId = Long.valueOf(request.get("userId").toString());
            String customerName = request.get("customerName").toString();
            Integer complaintType = Integer.valueOf(request.get("complaintType").toString());
            String content = request.get("content").toString();
            String images = request.getOrDefault("images", "").toString();
            
            // 验证订单是否存在
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                throw new BusinessException("订单不存在");
            }
            
            // 创建投诉记录
            com.milktea.entity.Complaint complaint = new com.milktea.entity.Complaint();
            complaint.setOrderId(orderId);
            complaint.setOrderNo(orderNo);
            complaint.setUserId(userId);
            complaint.setCustomerName(customerName);
            complaint.setComplaintType(complaintType);
            complaint.setContent(content);
            complaint.setImages(images);
            complaint.setStatus(0); // 待处理
            
            complaintMapper.insert(complaint);
            
            log.info("投诉提交成功: 订单ID={}, 投诉类型={}", orderId, complaintType);
            return Result.success("投诉提交成功");
        } catch (Exception e) {
            log.error("提交投诉失败", e);
            return Result.error("提交投诉失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户投诉列表
     */
    public Result<PageResult<Map<String, Object>>> getUserComplaints(Long userId, Integer page, Integer size) {
        try {
            Page<com.milktea.entity.Complaint> pageObj = new Page<>(page, size);
            
            LambdaQueryWrapper<com.milktea.entity.Complaint> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(com.milktea.entity.Complaint::getUserId, userId);
            queryWrapper.orderByDesc(com.milktea.entity.Complaint::getCreateTime);
            
            Page<com.milktea.entity.Complaint> result = complaintMapper.selectPage(pageObj, queryWrapper);
            
            // 转换为Map格式
            List<Map<String, Object>> records = result.getRecords().stream().map(complaint -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", complaint.getId());
                map.put("orderId", complaint.getOrderId());
                map.put("orderNo", complaint.getOrderNo());
                map.put("complaintType", complaint.getComplaintType());
                map.put("content", complaint.getContent());
                map.put("images", complaint.getImages());
                map.put("status", complaint.getStatus());
                map.put("response", complaint.getResponse());
                map.put("processTime", complaint.getProcessTime());
                map.put("createTime", complaint.getCreateTime());
                return map;
            }).toList();
            
            PageResult<Map<String, Object>> pageResult = new PageResult<>(
                records,
                result.getTotal(),
                result.getCurrent(),
                result.getSize()
            );
            
            return Result.success("获取成功", pageResult);
        } catch (Exception e) {
            log.error("获取投诉列表失败", e);
            return Result.error("获取失败");
        }
    }
    
}
