package com.milktea.controller;

import com.milktea.common.Result;
import com.milktea.dto.CouponDTO;
import com.milktea.dto.UserCouponDTO;
import com.milktea.entity.Coupon;
import com.milktea.service.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 优惠券控制器
 * @author MilkTea Team
 */
@Slf4j
@RestController
@RequestMapping("/coupon")
public class CouponController {
    
    private final CouponService couponService;
    
    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }
    
    /**
     * 获取可用优惠券列表
     */
    @GetMapping("/available")
    public Result<?> getAvailableCoupons(@RequestParam(required = false) Long userId) {
        // 如果传了userId，返回包含领取状态的列表
        if (userId != null) {
            return couponService.getAvailableCouponsWithStatus(userId);
        }
        return couponService.getAvailableCoupons();
    }
    
    /**
     * 领取优惠券
     */
    @PostMapping("/receive/{id}")
    public Result<String> receiveCoupon(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        return couponService.receiveCoupon(id, userId);
    }
    
    /**
     * 获取用户优惠券列表（包含优惠券详情）
     */
    @GetMapping("/my")
    public Result<List<UserCouponDTO>> getUserCoupons(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer status) {
        return couponService.getUserCoupons(userId, status);
    }
}
