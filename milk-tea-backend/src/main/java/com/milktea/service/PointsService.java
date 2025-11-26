package com.milktea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.milktea.common.Result;
import com.milktea.entity.PointsHistory;
import com.milktea.entity.User;
import com.milktea.exception.BusinessException;
import com.milktea.mapper.PointsHistoryMapper;
import com.milktea.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 积分服务
 * @author MilkTea Team
 */
@Slf4j
@Service
public class PointsService {
    
    private final PointsHistoryMapper pointsHistoryMapper;
    private final UserMapper userMapper;
    
    public PointsService(PointsHistoryMapper pointsHistoryMapper, UserMapper userMapper) {
        this.pointsHistoryMapper = pointsHistoryMapper;
        this.userMapper = userMapper;
    }
    
    /**
     * 添加积分
     * @param userId 用户ID
     * @param points 积分数量
     * @param type 类型：1-购物，2-签到，3-邀请，4-评价，5-生日
     * @param description 描述
     * @param orderId 订单ID（可选）
     */
    @Transactional
    public Result<String> addPoints(Long userId, Integer points, Integer type, String description, Long orderId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 更新用户积分
        user.setPoints((user.getPoints() == null ? 0 : user.getPoints()) + points);
        
        // 根据积分更新会员等级
        updateMemberLevel(user);
        
        userMapper.updateById(user);
        
        // 记录积分历史
        PointsHistory history = new PointsHistory();
        history.setUserId(userId);
        history.setPoints(points);
        history.setType(type);
        history.setDescription(description);
        history.setOrderId(orderId);
        pointsHistoryMapper.insert(history);
        
        log.info("用户{}获得{}积分，类型：{}，描述：{}", userId, points, type, description);
        
        return Result.success("积分添加成功");
    }
    
    /**
     * 扣除积分
     */
    @Transactional
    public Result<String> deductPoints(Long userId, Integer points, String description) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        if (user.getPoints() == null || user.getPoints() < points) {
            throw new BusinessException("积分不足");
        }
        
        // 更新用户积分
        user.setPoints(user.getPoints() - points);
        
        // 根据积分更新会员等级
        updateMemberLevel(user);
        
        userMapper.updateById(user);
        
        // 记录积分历史
        PointsHistory history = new PointsHistory();
        history.setUserId(userId);
        history.setPoints(-points);
        history.setType(6); // 6-兑换
        history.setDescription(description);
        pointsHistoryMapper.insert(history);
        
        return Result.success("积分扣除成功");
    }
    
    /**
     * 获取积分历史（分页）
     */
    public com.milktea.common.PageResult<PointsHistory> getHistory(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<PointsHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PointsHistory::getUserId, userId)
                   .orderByDesc(PointsHistory::getCreateTime);
        
        // 简单分页实现
        int offset = (page - 1) * size;
        queryWrapper.last("LIMIT " + offset + ", " + size);
        
        List<PointsHistory> historyList = pointsHistoryMapper.selectList(queryWrapper);
        
        // 获取总数
        LambdaQueryWrapper<PointsHistory> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(PointsHistory::getUserId, userId);
        Long total = pointsHistoryMapper.selectCount(countWrapper);
        
        return new com.milktea.common.PageResult<>(historyList, total, (long) page, (long) size);
    }
    
    /**
     * 获取积分商品列表（暂时返回空列表）
     */
    public com.milktea.common.PageResult<com.milktea.entity.PointsProduct> getProducts(Integer page, Integer size) {
        return new com.milktea.common.PageResult<>(new java.util.ArrayList<>(), 0L, (long) page, (long) size);
    }
    
    /**
     * 积分兑换（暂时简单实现）
     */
    @Transactional
    public void exchange(Long userId, Long productId, Integer quantity) {
        // 这里应该查询积分商品，扣除积分，创建兑换记录
        // 暂时简单实现：扣除100积分
        deductPoints(userId, 100 * quantity, "积分兑换商品");
    }
    
    /**
     * 每日签到
     */
    @Transactional
    public void checkin(Long userId) {
        // 检查今天是否已签到
        java.time.LocalDate today = java.time.LocalDate.now();
        
        // 这里应该查询签到记录表，判断是否已签到
        // 暂时简单实现：直接添加积分
        addPoints(userId, 10, 2, "每日签到", null);
    }
    
    /**
     * 根据积分更新会员等级
     */
    private void updateMemberLevel(User user) {
        Integer points = user.getPoints() == null ? 0 : user.getPoints();
        Integer newLevel;
        
        if (points >= 5000) {
            newLevel = 2; // 钻石会员
        } else if (points >= 1000) {
            newLevel = 1; // 黄金会员
        } else {
            newLevel = 0; // 普通会员
        }
        
        if (!newLevel.equals(user.getMemberLevel())) {
            user.setMemberLevel(newLevel);
            log.info("用户{}会员等级更新为：{}", user.getId(), newLevel);
        }
    }
    
    /**
     * 获取会员统计数据
     */
    public Result<Map<String, Object>> getMemberStats(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        Map<String, Object> stats = new HashMap<>();
        
        // 获取订单统计
        // 这里应该从订单表查询，暂时返回基础数据
        stats.put("totalOrders", 0);
        stats.put("totalAmount", 0.0);
        stats.put("totalPoints", user.getPoints() == null ? 0 : user.getPoints());
        stats.put("inviteCount", 0);
        
        return Result.success("获取成功", stats);
    }
}
