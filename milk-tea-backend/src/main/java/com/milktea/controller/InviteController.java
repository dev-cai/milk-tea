package com.milktea.controller;

import com.milktea.common.Result;
import com.milktea.entity.InviteRecord;
import com.milktea.service.InviteService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 邀请控制器
 * @author MilkTea Team
 */
@Slf4j
@RestController
@RequestMapping("/invite")
public class InviteController {
    
    private final InviteService inviteService;
    
    public InviteController(InviteService inviteService) {
        this.inviteService = inviteService;
    }
    
    /**
     * 获取我的邀请码
     */
    @GetMapping("/code")
    public Result<String> getInviteCode(@RequestParam(required = false) Long userId, HttpServletRequest request) {
        return inviteService.getInviteCode(currentUser(userId, request));
    }
    
    /**
     * 绑定邀请码
     */
    @PostMapping("/bind")
    public Result<String> bindInviteCode(@RequestParam(required = false) Long userId, @RequestParam String inviteCode, HttpServletRequest request) {
        return inviteService.bindInviteCode(currentUser(userId, request), inviteCode);
    }
    
    /**
     * 获取我邀请的好友列表
     */
    @GetMapping("/my-invites")
    public Result<List<InviteRecord>> getMyInvites(@RequestParam(required = false) Long userId, HttpServletRequest request) {
        return inviteService.getMyInvites(currentUser(userId, request));
    }

    private Long currentUser(Long supplied, HttpServletRequest request) {
        Object value = request.getAttribute("authenticatedUserId");
        if (value == null) throw new org.springframework.security.access.AccessDeniedException("未登录");
        Long current = Long.valueOf(value.toString());
        if (supplied != null && !current.equals(supplied)) throw new org.springframework.security.access.AccessDeniedException("无权操作其他用户数据");
        return current;
    }
}
