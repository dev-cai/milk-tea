package com.milktea.controller;

import com.milktea.common.Result;
import com.milktea.entity.InviteRecord;
import com.milktea.service.InviteService;
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
    public Result<String> getInviteCode(@RequestParam Long userId) {
        return inviteService.getInviteCode(userId);
    }
    
    /**
     * 绑定邀请码
     */
    @PostMapping("/bind")
    public Result<String> bindInviteCode(@RequestParam Long userId, @RequestParam String inviteCode) {
        return inviteService.bindInviteCode(userId, inviteCode);
    }
    
    /**
     * 获取我邀请的好友列表
     */
    @GetMapping("/my-invites")
    public Result<List<InviteRecord>> getMyInvites(@RequestParam Long userId) {
        return inviteService.getMyInvites(userId);
    }
}
