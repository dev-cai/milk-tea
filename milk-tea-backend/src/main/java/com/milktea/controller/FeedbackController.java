package com.milktea.controller;

import com.milktea.common.PageResult;
import com.milktea.common.Result;
import com.milktea.entity.Feedback;
import com.milktea.service.FeedbackService;
import com.milktea.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 反馈控制器
 */
@Slf4j
@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {
    
    private final FeedbackService feedbackService;
    private final JwtUtils jwtUtils;
    
    /**
     * 提交反馈
     */
    @PostMapping("/submit")
    public Result<Void> submitFeedback(@RequestBody Feedback feedback, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            Long userId = jwtUtils.getUserIdFromToken(token);
            feedback.setUserId(userId);
        }
        
        log.info("提交反馈，userId: {}, type: {}", feedback.getUserId(), feedback.getType());
        feedbackService.submitFeedback(feedback);
        return Result.success();
    }
    
    /**
     * 获取用户反馈列表
     */
    @GetMapping("/list")
    public Result<PageResult<Feedback>> getUserFeedbackList(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        log.info("查询用户反馈列表，userId: {}, page: {}, size: {}", userId, page, size);
        return feedbackService.getUserFeedbackList(userId, page, size);
    }
    
    /**
     * 获取反馈详情
     */
    @GetMapping("/{id}")
    public Result<Feedback> getFeedbackDetail(@PathVariable Long id) {
        log.info("查询反馈详情，id: {}", id);
        Feedback feedback = feedbackService.getFeedbackDetail(id);
        return Result.success(feedback);
    }
}
