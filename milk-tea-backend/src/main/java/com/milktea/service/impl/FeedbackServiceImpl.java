package com.milktea.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.milktea.common.PageResult;
import com.milktea.common.Result;
import com.milktea.entity.Feedback;
import com.milktea.exception.BusinessException;
import com.milktea.mapper.FeedbackMapper;
import com.milktea.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 反馈服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    
    private final FeedbackMapper feedbackMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitFeedback(Feedback feedback) {
        // 设置初始状态为待处理
        feedback.setStatus(0);
        
        int result = feedbackMapper.insert(feedback);
        if (result <= 0) {
            throw new BusinessException("提交反馈失败");
        }
        log.info("反馈提交成功，id: {}", feedback.getId());
    }
    
    @Override
    public Result<PageResult<Feedback>> getUserFeedbackList(Long userId, Integer page, Integer size) {
        Page<Feedback> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Feedback::getUserId, userId)
               .orderByDesc(Feedback::getCreateTime);
        
        Page<Feedback> pageResult = feedbackMapper.selectPage(pageParam, wrapper);
        
        PageResult<Feedback> result = PageResult.of(pageResult);
        
        log.info("查询结果 - 记录数: {}, 总数: {}, 当前页: {}, 每页大小: {}", 
                 pageResult.getRecords().size(), pageResult.getTotal(), page, size);
        
        return Result.success(result);
    }
    
    @Override
    public Feedback getFeedbackDetail(Long id) {
        Feedback feedback = feedbackMapper.selectById(id);
        if (feedback == null) {
            throw new BusinessException("反馈不存在");
        }
        return feedback;
    }
    
    @Override
    public Result<PageResult<Feedback>> getAdminFeedbackList(Integer status, Integer page, Integer size) {
        Page<Feedback> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Feedback::getStatus, status);
        }
        wrapper.orderByDesc(Feedback::getCreateTime);
        
        Page<Feedback> pageResult = feedbackMapper.selectPage(pageParam, wrapper);
        
        PageResult<Feedback> result = PageResult.of(pageResult);
        
        return Result.success(result);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replyFeedback(Long id, String reply) {
        Feedback feedback = feedbackMapper.selectById(id);
        if (feedback == null) {
            throw new BusinessException("反馈不存在");
        }
        
        feedback.setReply(reply);
        feedback.setReplyTime(LocalDateTime.now());
        feedback.setStatus(2); // 已回复
        
        int result = feedbackMapper.updateById(feedback);
        if (result <= 0) {
            throw new BusinessException("回复失败");
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Feedback feedback = feedbackMapper.selectById(id);
        if (feedback == null) {
            throw new BusinessException("反馈不存在");
        }
        
        feedback.setStatus(status);
        
        int result = feedbackMapper.updateById(feedback);
        if (result <= 0) {
            throw new BusinessException("更新状态失败");
        }
    }
}
