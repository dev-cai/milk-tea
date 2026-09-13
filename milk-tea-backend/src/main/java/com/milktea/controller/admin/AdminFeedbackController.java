package com.milktea.controller.admin;

import com.milktea.common.PageResult;
import com.milktea.common.Result;
import com.milktea.entity.Feedback;
import com.milktea.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理端反馈控制器
 */
@RestController
@RequestMapping("/admin/feedback")
@RequiredArgsConstructor
public class AdminFeedbackController {
    
    private final FeedbackService feedbackService;
    
    /**
     * 获取反馈列表
     */
    @GetMapping("/list")
    public Result<PageResult<Feedback>> getFeedbackList(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        return feedbackService.getAdminFeedbackList(status, page, size);
    }
    
    /**
     * 获取反馈详情
     */
    @GetMapping("/{id}")
    public Result<Feedback> getFeedbackDetail(@PathVariable Long id) {
        Feedback feedback = feedbackService.getFeedbackDetail(id);
        return Result.success(feedback);
    }
    
    /**
     * 回复反馈
     */
    @PostMapping("/reply")
    public Result<Void> replyFeedback(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        String reply = params.get("reply").toString();
        
        feedbackService.replyFeedback(id, reply);
        return Result.success();
    }
    
    /**
     * 更新反馈状态
     */
    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Integer status = Integer.valueOf(params.get("status").toString());
        
        feedbackService.updateStatus(id, status);
        return Result.success();
    }
}
