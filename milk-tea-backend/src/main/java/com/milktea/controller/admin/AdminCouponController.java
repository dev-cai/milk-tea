package com.milktea.controller.admin;

import com.milktea.common.PageResult;
import com.milktea.common.Result;
import com.milktea.entity.Coupon;
import com.milktea.service.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


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
            @RequestParam(required = false) String keyword) {
        // 简化实现，返回空结果
        PageResult<Coupon> pageResult = new PageResult<>();
        return Result.success("查询成功", pageResult);
    }
    
    /**
     * 添加优惠券
     */
    @PostMapping
    public Result<String> addCoupon(@RequestBody Coupon coupon) {
        return Result.success("添加成功");
    }
    
    /**
     * 更新优惠券
     */
    @PutMapping("/{id}")
    public Result<String> updateCoupon(@PathVariable Long id, @RequestBody Coupon coupon) {
        coupon.setId(id);
        return Result.success("更新成功");
    }
    
    /**
     * 删除优惠券
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteCoupon(@PathVariable Long id) {
        return Result.success("删除成功");
    }
}
