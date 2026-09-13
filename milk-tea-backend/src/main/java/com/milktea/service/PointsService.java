package com.milktea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.milktea.common.Result;
import com.milktea.entity.PointsHistory;
import com.milktea.entity.User;
import com.milktea.exception.BusinessException;
import com.milktea.mapper.PointsHistoryMapper;
import com.milktea.mapper.UserMapper;
import com.milktea.mapper.PointsProductMapper;
import com.milktea.mapper.CheckinRecordMapper;
import com.milktea.entity.CheckinRecord;
import com.milktea.entity.PointsProduct;
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
    private final PointsProductMapper pointsProductMapper;
    private final CheckinRecordMapper checkinRecordMapper;
    
    public PointsService(PointsHistoryMapper pointsHistoryMapper, UserMapper userMapper, PointsProductMapper pointsProductMapper, CheckinRecordMapper checkinRecordMapper) {
        this.pointsHistoryMapper = pointsHistoryMapper;
        this.userMapper = userMapper;
        this.pointsProductMapper = pointsProductMapper;
        this.checkinRecordMapper = checkinRecordMapper;
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
        
        if (points == null || points <= 0 || userMapper.deductPointsIfEnough(userId, points) != 1) throw new BusinessException("积分不足");
        user = userMapper.selectById(userId);
        updateMemberLevel(user);
        com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<User> levelUpdate = new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<>();
        levelUpdate.eq(User::getId, userId).set(User::getMemberLevel, user.getMemberLevel());
        userMapper.update(null, levelUpdate);
        
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
        int safePage = page == null ? 1 : Math.max(1, page);
        int safeSize = size == null ? 10 : Math.max(1, Math.min(size, 100));
        int offset = (safePage - 1) * safeSize;
        queryWrapper.last("LIMIT " + offset + ", " + safeSize);
        
        List<PointsHistory> historyList = pointsHistoryMapper.selectList(queryWrapper);
        
        // 获取总数
        LambdaQueryWrapper<PointsHistory> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(PointsHistory::getUserId, userId);
        Long total = pointsHistoryMapper.selectCount(countWrapper);
        
        return new com.milktea.common.PageResult<>(historyList, total, (long) safePage, (long) safeSize);
    }
    
    public com.milktea.common.PageResult<com.milktea.entity.PointsProduct> getProducts(Integer page, Integer size) {
        int safePage = page == null ? 1 : Math.max(1, page);
        int safeSize = size == null ? 10 : Math.min(100, Math.max(1, size));
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<PointsProduct> p =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(safePage, safeSize);
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PointsProduct> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(PointsProduct::getStatus, 1).orderByAsc(PointsProduct::getSort).orderByDesc(PointsProduct::getCreateTime);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<PointsProduct> result = pointsProductMapper.selectPage(p, wrapper);
        return new com.milktea.common.PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    @Transactional
    public void exchange(Long userId, Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0 || quantity > 20) throw new BusinessException("兑换数量不合法");
        PointsProduct product = pointsProductMapper.selectById(productId);
        if (product == null || product.getStatus() != 1 || product.getDeleted() != 0) throw new BusinessException("积分商品不存在或已下架");
        if (product.getStock() == null || product.getStock() < quantity) throw new BusinessException("库存不足");
        int total = product.getPoints() * quantity;
        if (pointsProductMapper.decrementStockIfAvailable(productId, quantity) != 1) throw new BusinessException("库存不足");
        deductPoints(userId, total, "积分兑换" + product.getName());
    }
    
    /**
     * 每日签到
     */
    @Transactional
    public void checkin(Long userId) {
        java.time.LocalDate today = java.time.LocalDate.now();
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CheckinRecord> query = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        query.eq(CheckinRecord::getUserId, userId).eq(CheckinRecord::getCheckinDate, today);
        if (checkinRecordMapper.selectCount(query) > 0) throw new BusinessException("今天已经签到");
        CheckinRecord record = new CheckinRecord();
        record.setUserId(userId);
        record.setCheckinDate(today);
        record.setContinuousDays(1);
        record.setPoints(10);
        checkinRecordMapper.insert(record);
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
