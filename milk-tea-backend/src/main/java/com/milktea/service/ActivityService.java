package com.milktea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.milktea.entity.Activity;
import com.milktea.mapper.ActivityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 营销活动服务类
 */
@Service
@RequiredArgsConstructor
public class ActivityService {
    
    private final ActivityMapper activityMapper;
    
    /**
     * 分页查询活动
     */
    public Page<Activity> page(int page, int size, String keyword) {
        Page<Activity> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Activity::getName, keyword);
        }
        
        wrapper.orderByDesc(Activity::getCreateTime);
        return activityMapper.selectPage(pageParam, wrapper);
    }
    
    /**
     * 获取进行中的活动
     */
    public List<Activity> getActiveList() {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Activity::getStatus, 1);
        wrapper.le(Activity::getStartTime, LocalDateTime.now());
        wrapper.ge(Activity::getEndTime, LocalDateTime.now());
        wrapper.orderByDesc(Activity::getCreateTime);
        return activityMapper.selectList(wrapper);
    }
    
    /**
     * 根据ID获取活动
     */
    public Activity getById(Long id) {
        return activityMapper.selectById(id);
    }
    
    /**
     * 创建活动
     */
    public void create(Activity activity) {
        activityMapper.insert(activity);
    }
    
    /**
     * 更新活动
     */
    public void update(Activity activity) {
        activityMapper.updateById(activity);
    }
    
    /**
     * 删除活动
     */
    public void delete(Long id) {
        activityMapper.deleteById(id);
    }
    
    /**
     * 更新状态
     */
    public void updateStatus(Long id, Integer status) {
        Activity activity = new Activity();
        activity.setId(id);
        activity.setStatus(status);
        activityMapper.updateById(activity);
    }
}
