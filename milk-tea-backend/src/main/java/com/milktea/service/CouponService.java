package com.milktea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.milktea.common.Result;
import com.milktea.dto.CouponDTO;
import com.milktea.dto.UserCouponDTO;
import com.milktea.entity.Coupon;
import com.milktea.entity.UserCoupon;
import com.milktea.exception.BusinessException;
import com.milktea.mapper.CouponMapper;
import com.milktea.mapper.UserCouponMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
     * 获取可领取优惠券列表（返回所有优惠券，并标记已领取状态）
     */
    public Result<List<CouponDTO>> getAvailableCouponsWithStatus(Long userId) {
        LambdaQueryWrapper<Coupon> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Coupon::getStatus, 1)
                   .le(Coupon::getValidStart, LocalDateTime.now())
                   .ge(Coupon::getValidEnd, LocalDateTime.now())
                   .orderByDesc(Coupon::getCreateTime);
        
        List<Coupon> coupons = couponMapper.selectList(queryWrapper);
        
        // 查询用户已领取的优惠券ID列表
        List<Long> receivedCouponIds = new ArrayList<>();
        if (userId != null) {
            LambdaQueryWrapper<UserCoupon> userCouponQuery = new LambdaQueryWrapper<>();
            userCouponQuery.eq(UserCoupon::getUserId, userId)
                          .select(UserCoupon::getCouponId);
            List<UserCoupon> userCoupons = userCouponMapper.selectList(userCouponQuery);
            receivedCouponIds = userCoupons.stream()
                                          .map(UserCoupon::getCouponId)
                                          .collect(Collectors.toList());
        }
        
        // 返回所有优惠券，并标记已领取状态
        List<Long> finalReceivedCouponIds = receivedCouponIds;
        List<CouponDTO> couponDTOs = coupons.stream()
                .map(coupon -> {
                    CouponDTO dto = new CouponDTO();
                    BeanUtils.copyProperties(coupon, dto);
                    // 标记是否已领取
                    dto.setIsReceived(finalReceivedCouponIds.contains(coupon.getId()));
                    return dto;
                }).collect(Collectors.toList());
        
        return Result.success("获取成功", couponDTOs);
    }
    
    /**
     * 领取优惠券
     */
    @Transactional
    public Result<String> receiveCoupon(Long couponId, Long userId) {
        log.info("用户 {} 领取优惠券 {}", userId, couponId);
        
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
        int insertResult = userCouponMapper.insert(userCoupon);
        
        log.info("插入用户优惠券记录，结果: {}, ID: {}", insertResult, userCoupon.getId());
        
        // 更新优惠券领取数量
        coupon.setReceivedCount(coupon.getReceivedCount() + 1);
        couponMapper.updateById(coupon);
        
        log.info("优惠券 {} 领取成功，已领取数量: {}", couponId, coupon.getReceivedCount());
        
        return Result.success("领取成功");
    }
    
    /**
     * 获取用户优惠券列表（包含优惠券详情）
     */
    public Result<List<UserCouponDTO>> getUserCoupons(Long userId, Integer status) {
        log.info("获取用户优惠券列表 - userId: {}, status: {}", userId, status);
        
        // 使用关联查询获取用户优惠券及优惠券详情
        List<UserCouponDTO> userCoupons = userCouponMapper.selectUserCouponsWithDetail(userId, status);
        
        log.info("查询到 {} 张优惠券", userCoupons.size());
        
        // 检查并更新过期状态
        LocalDateTime now = LocalDateTime.now();
        for (UserCouponDTO dto : userCoupons) {
            if (dto.getStatus() == 0 && dto.getEndTime() != null && now.isAfter(dto.getEndTime())) {
                // 更新为已过期
                UserCoupon userCoupon = new UserCoupon();
                userCoupon.setId(dto.getId());
                userCoupon.setStatus(2);
                userCouponMapper.updateById(userCoupon);
                dto.setStatus(2);
                log.info("优惠券 {} 已过期，更新状态", dto.getId());
            }
        }
        
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
