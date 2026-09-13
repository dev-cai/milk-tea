package com.milktea.controller.admin;

import com.milktea.common.PageResult;
import com.milktea.common.Result;
import com.milktea.entity.Coupon;
import com.milktea.service.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * 管理端优惠券控制器
 * @author MilkTea Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/coupon")
public class AdminCouponController {
    
    private final CouponService couponService;
    
    public AdminCouponController(CouponService couponService) {
        this.couponService = couponService;
    }
    
    /**
     * 分页查询优惠券
     */
    @GetMapping("/page")
    public Result<PageResult<Coupon>> getCouponPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        return couponService.getAdminCouponPage(page, size, keyword, type, status);
    }

    @GetMapping("/{id}")
    public Result<Coupon> getCoupon(@PathVariable Long id) {
        return couponService.getAdminCoupon(id);
    }
    
    /**
     * 添加优惠券
     */
    @PostMapping
    public Result<String> addCoupon(@RequestBody Coupon coupon) {
        return couponService.addAdminCoupon(coupon);
    }
    
    /**
     * 更新优惠券
     */
    @PutMapping("/{id}")
    public Result<String> updateCoupon(@PathVariable Long id, @RequestBody Coupon coupon) {
        coupon.setId(id);
        return couponService.updateAdminCoupon(coupon);
    }
    
    /**
     * 删除优惠券
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteCoupon(@PathVariable Long id) {
        return couponService.deleteAdminCoupon(id);
    }

    @GetMapping("/{id}/usage")
    public Result<List<Map<String, Object>>> getCouponUsage(@PathVariable Long id) {
        return couponService.getAdminCouponUsage(id);
    }

    @DeleteMapping("/batch")
    public Result<String> batchDeleteCoupons(@RequestBody List<Long> ids) {
        return couponService.batchDeleteAdminCoupons(ids);
    }
}
