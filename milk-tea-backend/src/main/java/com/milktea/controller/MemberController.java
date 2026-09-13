package com.milktea.controller;

import com.milktea.common.Result;
import com.milktea.entity.User;
import com.milktea.mapper.UserMapper;
import com.milktea.service.InviteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.*;

/**
 * 会员控制器
 * @author MilkTea Team
 */
@Slf4j
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    
    private final UserMapper userMapper;
    private final InviteService inviteService;
    
    /**
     * 获取会员信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getInfo(@RequestParam(required = false) Long userId, HttpServletRequest request) {
        Long currentUserId = currentUser(userId, request);
        User user = userMapper.selectById(currentUserId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        Map<String, Object> memberInfo = new HashMap<>();
        memberInfo.put("id", user.getId());
        memberInfo.put("memberLevel", user.getMemberLevel());
        memberInfo.put("levelName", getMemberLevelName(user.getMemberLevel()));
        memberInfo.put("points", user.getPoints());
        memberInfo.put("balance", user.getBalance());
        
        // 计算升级所需积分
        int nextLevelPoints = getNextLevelPoints(user.getMemberLevel());
        memberInfo.put("nextLevelPoints", nextLevelPoints);
        
        // 获取会员特权
        List<String> privileges = getMemberPrivileges(user.getMemberLevel());
        memberInfo.put("privileges", privileges);
        
        return Result.success(memberInfo);
    }
    
    /**
     * 获取会员特权
     */
    @GetMapping("/privileges")
    public Result<List<Map<String, Object>>> getPrivileges(@RequestParam Integer level) {
        List<Map<String, Object>> privileges = new ArrayList<>();
        
        // 根据等级返回不同的特权
        if (level >= 0) {
            privileges.add(createPrivilege("积分奖励", "消费可获得积分", "🎯"));
            privileges.add(createPrivilege("生日特权", "生日当月专属优惠", "🎂"));
        }
        
        if (level >= 1) {
            privileges.add(createPrivilege("专享折扣", "全场商品9.5折", "💰"));
            privileges.add(createPrivilege("优先配送", "订单优先处理", "🚀"));
        }
        
        if (level >= 2) {
            privileges.add(createPrivilege("积分翻倍", "消费积分双倍奖励", "✨"));
            privileges.add(createPrivilege("免费配送", "全场免配送费", "🎁"));
            privileges.add(createPrivilege("专属客服", "VIP专属客服", "👑"));
        }
        
        return Result.success(privileges);
    }
    
    /**
     * 获取会员统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats(@RequestParam(required = false) Long userId, HttpServletRequest request) {
        Long currentUserId = currentUser(userId, request);
        User user = userMapper.selectById(currentUserId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        Map<String, Object> stats = new HashMap<>();
        
        // 查询订单统计
        try {
            Map<String, Object> orderStats = userMapper.selectOrderStats(currentUserId);
            if (orderStats != null) {
                stats.put("totalOrders", orderStats.get("totalOrders"));
                stats.put("totalAmount", orderStats.get("totalAmount"));
            } else {
                stats.put("totalOrders", 0);
                stats.put("totalAmount", 0.0);
            }
        } catch (org.springframework.security.access.AccessDeniedException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询订单统计失败", e);
            stats.put("totalOrders", 0);
            stats.put("totalAmount", 0.0);
        }
        
        stats.put("totalPoints", user.getPoints() == null ? 0 : user.getPoints());
        
        // 查询邀请统计
        try {
            Map<String, Object> inviteStats = userMapper.selectInviteStats(currentUserId);
            if (inviteStats != null) {
                stats.put("inviteCount", inviteStats.get("inviteCount"));
            } else {
                stats.put("inviteCount", 0);
            }
        } catch (Exception e) {
            log.error("查询邀请统计失败", e);
            stats.put("inviteCount", 0);
        }
        
        return Result.success("获取成功", stats);
    }
    
    /**
     * 邀请好友
     */
    @PostMapping("/invite")
    public Result<Map<String, Object>> invite(@RequestBody Map<String, Object> request, HttpServletRequest servletRequest) {
        try {
            Long userId = currentUser(request.get("userId") == null ? null : Long.valueOf(request.get("userId").toString()), servletRequest);
            
            User user = userMapper.selectById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            String inviteCode = inviteService.getInviteCode(userId).getData();
            
            Map<String, Object> result = new HashMap<>();
            result.put("inviteCode", inviteCode);
            result.put("inviteUrl", "https://milktea.com/invite?code=" + inviteCode);
            result.put("reward", "邀请好友注册，双方各得50积分");
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("生成邀请码失败", e);
            return Result.error("操作失败");
        }
    }
    
    /**
     * 获取会员等级名称
     */
    private String getMemberLevelName(Integer level) {
        switch (level) {
            case 0: return "普通会员";
            case 1: return "黄金会员";
            case 2: return "钻石会员";
            default: return "普通会员";
        }
    }

    private Long currentUser(Long suppliedUserId, HttpServletRequest request) {
        Object value = request.getAttribute("authenticatedUserId");
        if (value == null) throw new org.springframework.security.access.AccessDeniedException("未登录");
        Long current = Long.valueOf(value.toString());
        if (suppliedUserId != null && !current.equals(suppliedUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("无权操作其他用户数据");
        }
        return current;
    }
    
    /**
     * 获取升级所需积分
     */
    private int getNextLevelPoints(Integer currentLevel) {
        switch (currentLevel) {
            case 0: return 1000;  // 升级到黄金会员需要1000积分
            case 1: return 3000;  // 升级到钻石会员需要3000积分
            case 2: return 0;     // 已是最高等级
            default: return 1000;
        }
    }
    
    /**
     * 获取会员特权列表
     */
    private List<String> getMemberPrivileges(Integer level) {
        List<String> privileges = new ArrayList<>();
        
        privileges.add("积分奖励");
        privileges.add("生日特权");
        
        if (level >= 1) {
            privileges.add("专享折扣");
            privileges.add("优先配送");
        }
        
        if (level >= 2) {
            privileges.add("积分翻倍");
            privileges.add("免费配送");
            privileges.add("专属客服");
        }
        
        return privileges;
    }
    
    /**
     * 创建特权对象
     */
    private Map<String, Object> createPrivilege(String name, String description, String icon) {
        Map<String, Object> privilege = new HashMap<>();
        privilege.put("name", name);
        privilege.put("description", description);
        privilege.put("icon", icon);
        return privilege;
    }
}
