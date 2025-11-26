package com.milktea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.milktea.common.PageResult;
import com.milktea.common.Result;
import com.milktea.entity.MarketingActivity;
import com.milktea.exception.BusinessException;
import com.milktea.mapper.MarketingActivityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 营销活动服务类
 * @author MilkTea Team
 */
@Slf4j
@Service
public class MarketingActivityService {
    
    private final MarketingActivityMapper activityMapper;
    
    public MarketingActivityService(MarketingActivityMapper activityMapper) {
        this.activityMapper = activityMapper;
    }
    
    /**
     * 分页查询活动
     */
    public Result<PageResult<MarketingActivity>> getActivityPage(Integer page, Integer size, 
                                                                  String keyword, String type, Integer status) {
        try {
            Page<MarketingActivity> pageObj = new Page<>(page, size);
            
            LambdaQueryWrapper<MarketingActivity> queryWrapper = new LambdaQueryWrapper<>();
            
            if (StringUtils.hasText(keyword)) {
                queryWrapper.like(MarketingActivity::getName, keyword);
            }
            
            if (StringUtils.hasText(type)) {
                queryWrapper.eq(MarketingActivity::getType, type);
            }
            
            if (status != null) {
                queryWrapper.eq(MarketingActivity::getStatus, status);
            }
            
            queryWrapper.orderByDesc(MarketingActivity::getCreateTime);
            
            Page<MarketingActivity> result = activityMapper.selectPage(pageObj, queryWrapper);
            
            PageResult<MarketingActivity> pageResult = new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getCurrent(),
                result.getSize()
            );
            
            return Result.success("查询成功", pageResult);
        } catch (Exception e) {
            log.error("查询活动失败", e);
            return Result.error("查询失败");
        }
    }
    
    /**
     * 获取活动详情
     */
    public Result<MarketingActivity> getActivityById(Long id) {
        try {
            MarketingActivity activity = activityMapper.selectById(id);
            if (activity == null) {
                throw new BusinessException("活动不存在");
            }
            return Result.success("获取成功", activity);
        } catch (Exception e) {
            log.error("获取活动详情失败", e);
            return Result.error("获取失败");
        }
    }
    
    /**
     * 创建活动
     */
    public Result<String> createActivity(MarketingActivity activity) {
        try {
            // 设置初始状态
            if (activity.getStatus() == null) {
                LocalDateTime now = LocalDateTime.now();
                if (now.isBefore(activity.getStartTime())) {
                    activity.setStatus(0); // 未开始
                } else if (now.isAfter(activity.getEndTime())) {
                    activity.setStatus(2); // 已结束
                } else {
                    activity.setStatus(1); // 进行中
                }
            }
            
            if (activity.getParticipants() == null) {
                activity.setParticipants(0);
            }
            
            activityMapper.insert(activity);
            log.info("创建活动成功: {}", activity.getName());
            return Result.success("创建成功");
        } catch (Exception e) {
            log.error("创建活动失败", e);
            return Result.error("创建失败");
        }
    }
    
    /**
     * 更新活动
     */
    public Result<String> updateActivity(Long id, MarketingActivity activity) {
        try {
            MarketingActivity existActivity = activityMapper.selectById(id);
            if (existActivity == null) {
                throw new BusinessException("活动不存在");
            }
            
            activity.setId(id);
            activityMapper.updateById(activity);
            log.info("更新活动成功: {}", activity.getName());
            return Result.success("更新成功");
        } catch (Exception e) {
            log.error("更新活动失败", e);
            return Result.error("更新失败");
        }
    }
    
    /**
     * 删除活动
     */
    public Result<String> deleteActivity(Long id) {
        try {
            activityMapper.deleteById(id);
            log.info("删除活动成功: ID={}", id);
            return Result.success("删除成功");
        } catch (Exception e) {
            log.error("删除活动失败", e);
            return Result.error("删除失败");
        }
    }
    
    /**
     * 批量删除活动
     */
    public Result<String> batchDeleteActivities(List<Long> ids) {
        try {
            activityMapper.deleteBatchIds(ids);
            log.info("批量删除活动成功: 数量={}", ids.size());
            return Result.success("批量删除成功");
        } catch (Exception e) {
            log.error("批量删除活动失败", e);
            return Result.error("批量删除失败");
        }
    }
    
    /**
     * 更新活动状态
     */
    public Result<String> updateActivityStatus(Long id, Integer status) {
        try {
            MarketingActivity activity = activityMapper.selectById(id);
            if (activity == null) {
                throw new BusinessException("活动不存在");
            }
            
            activity.setStatus(status);
            activityMapper.updateById(activity);
            log.info("更新活动状态成功: ID={}, 状态={}", id, status);
            return Result.success("状态更新成功");
        } catch (Exception e) {
            log.error("更新活动状态失败", e);
            return Result.error("状态更新失败");
        }
    }
    
    /**
     * 获取活动统计
     */
    public Result<Map<String, Object>> getActivityStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 总活动数
            Long total = activityMapper.selectCount(null);
            
            // 进行中的活动数
            LambdaQueryWrapper<MarketingActivity> activeWrapper = new LambdaQueryWrapper<>();
            activeWrapper.eq(MarketingActivity::getStatus, 1);
            Long active = activityMapper.selectCount(activeWrapper);
            
            // 总参与人数
            List<MarketingActivity> activities = activityMapper.selectList(null);
            int totalParticipants = activities.stream()
                    .mapToInt(MarketingActivity::getParticipants)
                    .sum();
            
            // 总收益
            double totalRevenue = activities.stream()
                    .mapToDouble(a -> a.getRevenue().doubleValue())
                    .sum();
            
            stats.put("total", total);
            stats.put("active", active);
            stats.put("participants", totalParticipants);
            stats.put("revenue", totalRevenue);
            
            return Result.success("获取成功", stats);
        } catch (Exception e) {
            log.error("获取活动统计失败", e);
            return Result.error("获取失败");
        }
    }
    
    /**
     * 获取活动分析数据
     */
    public Result<Map<String, Object>> getActivityAnalysis(Long id) {
        try {
            MarketingActivity activity = activityMapper.selectById(id);
            if (activity == null) {
                throw new BusinessException("活动不存在");
            }
            
            Map<String, Object> analysis = new HashMap<>();
            analysis.put("participants", activity.getParticipants());
            analysis.put("revenue", activity.getRevenue());
            analysis.put("avgRevenue", activity.getParticipants() > 0 ? 
                    activity.getRevenue().doubleValue() / activity.getParticipants() : 0);
            analysis.put("conversionRate", 0); // 简化实现
            
            return Result.success("获取成功", analysis);
        } catch (Exception e) {
            log.error("获取活动分析失败", e);
            return Result.error("获取失败");
        }
    }
    
    /**
     * 获取启用的活动列表（用户端）
     */
    public List<MarketingActivity> getActiveList() {
        LambdaQueryWrapper<MarketingActivity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MarketingActivity::getStatus, 1);
        wrapper.orderByDesc(MarketingActivity::getCreateTime);
        return activityMapper.selectList(wrapper);
    }
    
    /**
     * 根据ID获取活动
     */
    public MarketingActivity getById(Long id) {
        return activityMapper.selectById(id);
    }
    
    /**
     * 参与活动
     */
    public void joinActivity(Long activityId, Long userId) {
        MarketingActivity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }
        
        if (activity.getStatus() != 1) {
            throw new BusinessException("活动未开始或已结束");
        }
        
        // 增加参与人数
        activity.setParticipants(activity.getParticipants() + 1);
        activityMapper.updateById(activity);
        
        log.info("用户参与活动成功: userId={}, activityId={}", userId, activityId);
    }
}
