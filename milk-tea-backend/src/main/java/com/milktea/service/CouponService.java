package com.milktea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.milktea.common.Result;
import com.milktea.entity.Coupon;
import com.milktea.entity.UserCoupon;
import com.milktea.exception.BusinessException;
import com.milktea.mapper.CouponMapper;
import com.milktea.mapper.UserCouponMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠券服务类
 * @author MilkTea Team
 */
@Slf4j
@Service
public class CouponService {
    
    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;
    
    public CouponService(CouponMapper couponMapper, UserCouponMapper userCouponMapper) {
        this.couponMapper = couponMapper;
        this.userCouponMapper = userCouponMapper;
    }
    
    /**
     * 获取可用优惠券列表
     */
    public Result<List<Coupon>> getAvailableCoupons() {
        LambdaQueryWrapper<Coupon> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Coupon::getStatus, 1)
                   .le(Coupon::getValidStart, LocalDateTime.now())
                   .ge(Coupon::getValidEnd, LocalDateTime.now())
                   .orderByDesc(Coupon::getCreateTime);
        
        List<Coupon> coupons = couponMapper.selectList(queryWrapper);
        return Result.success("获取成功", coupons);
    }
    
    /**
     * 领取优惠券
     */
    @Transactional
    public Result<String> receiveCoupon(Long couponId, Long userId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        
        if (coupon.getStatus() == 0) {
            throw new BusinessException("优惠券已禁用");
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getValidStart()) || now.isAfter(coupon.getValidEnd())) {
            throw new BusinessException("优惠券不在有效期内");
        }
        
        if (coupon.getReceivedCount() >= coupon.getTotalCount()) {
            throw new BusinessException("优惠券已被领完");
        }
        
        // 检查用户是否已领取
        LambdaQueryWrapper<UserCoupon> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCoupon::getUserId, userId)
                   .eq(UserCoupon::getCouponId, couponId);
        UserCoupon existUserCoupon = userCouponMapper.selectOne(queryWrapper);
        
        if (existUserCoupon != null) {
            throw new BusinessException("您已领取过该优惠券");
        }
        
        // 创建用户优惠券记录
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setStatus(0); // 未使用
        userCouponMapper.insert(userCoupon);
        
        // 更新优惠券领取数量
        coupon.setReceivedCount(coupon.getReceivedCount() + 1);
        couponMapper.updateById(coupon);
        
        return Result.success("领取成功");
    }
    
    /**
     * 获取用户优惠券列表
     */
    public Result<List<UserCoupon>> getUserCoupons(Long userId, Integer status) {
        LambdaQueryWrapper<UserCoupon> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCoupon::getUserId, userId);
        
        if (status != null) {
            queryWrapper.eq(UserCoupon::getStatus, status);
        }
        
        queryWrapper.orderByDesc(UserCoupon::getCreateTime);
        
        List<UserCoupon> userCoupons = userCouponMapper.selectList(queryWrapper);
        return Result.success("获取成功", userCoupons);
    }
    
    /**
     * 使用优惠券
     */
    @Transactional
    public Result<String> useCoupon(Long userCouponId, Long orderId) {
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null) {
            throw new BusinessException("用户优惠券不存在");
        }
        
        if (userCoupon.getStatus() != 0) {
            throw new BusinessException("优惠券已使用或已过期");
        }
        
        // 检查优惠券是否过期
        Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());
        if (coupon != null && LocalDateTime.now().isAfter(coupon.getValidEnd())) {
            userCoupon.setStatus(2); // 已过期
            userCouponMapper.updateById(userCoupon);
            throw new BusinessException("优惠券已过期");
        }
        
        // 使用优惠券
        userCoupon.setStatus(1); // 已使用
        userCoupon.setOrderId(orderId);
        userCoupon.setUseTime(LocalDateTime.now());
        userCouponMapper.updateById(userCoupon);
        
        return Result.success("优惠券使用成功");
    }
}
