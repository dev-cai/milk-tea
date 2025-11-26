package com.milktea.service;

import com.milktea.common.PageResult;
import com.milktea.common.Result;
import com.milktea.entity.Feedback;

/**
 * 反馈服务接口
 */
public interface FeedbackService {
    
    /**
     * 提交反馈
     */
    void submitFeedback(Feedback feedback);
    
    /**
     * 获取用户反馈列表
     */
    Result<PageResult<Feedback>> getUserFeedbackList(Long userId, Integer page, Integer size);
    
    /**
     * 获取反馈详情
     */
    Feedback getFeedbackDetail(Long id);
    
    /**
     * 管理员获取反馈列表
     */
    Result<PageResult<Feedback>> getAdminFeedbackList(Integer status, Integer page, Integer size);
    
    /**
     * 管理员回复反馈
     */
    void replyFeedback(Long id, String reply);
    
    /**
     * 更新反馈状态
     */
    void updateStatus(Long id, Integer status);
}
