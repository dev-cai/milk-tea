package com.milktea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.milktea.common.PageResult;
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
import java.math.BigDecimal;
import java.util.Map;

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

    /** 管理端优惠券分页查询。 */
    public Result<PageResult<Coupon>> getAdminCouponPage(Integer page, Integer size, String keyword,
                                                          Integer type, Integer status) {
        Page<Coupon> pageObj = new Page<>(page == null ? 1 : Math.max(1, page),
                size == null ? 10 : Math.min(100, Math.max(1, size)));
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coupon::getDeleted, 0);
        if (keyword != null && !keyword.trim().isEmpty()) wrapper.like(Coupon::getName, keyword.trim());
        if (type != null) wrapper.eq(Coupon::getType, type);
        if (status != null) wrapper.eq(Coupon::getStatus, status);
        wrapper.orderByDesc(Coupon::getCreateTime);
        Page<Coupon> result = couponMapper.selectPage(pageObj, wrapper);
        return Result.success("查询成功", PageResult.of(result));
    }

    public Result<Coupon> getAdminCoupon(Long id) {
        Coupon coupon = couponMapper.selectById(id);
        if (coupon == null || coupon.getDeleted() != null && coupon.getDeleted() == 1) {
            throw new BusinessException("优惠券不存在");
        }
        return Result.success("获取成功", coupon);
    }

    @Transactional
    public Result<String> addAdminCoupon(Coupon coupon) {
        validateCoupon(coupon, false);
        coupon.setId(null);
        if (coupon.getReceivedCount() == null) coupon.setReceivedCount(0);
        if (coupon.getStatus() == null) coupon.setStatus(1);
        couponMapper.insert(coupon);
        return Result.success("添加成功");
    }

    @Transactional
    public Result<String> updateAdminCoupon(Coupon coupon) {
        Coupon existing = couponMapper.selectById(coupon.getId());
        if (existing == null) throw new BusinessException("优惠券不存在");
        validateCoupon(coupon, true);
        // 部分更新（例如状态开关）不能把原有配置字段清成 NULL。
        if (coupon.getName() == null) coupon.setName(existing.getName());
        if (coupon.getType() == null) coupon.setType(existing.getType());
        if (coupon.getDiscount() == null) coupon.setDiscount(existing.getDiscount());
        if (coupon.getMinAmount() == null) coupon.setMinAmount(existing.getMinAmount());
        if (coupon.getTotalCount() == null) coupon.setTotalCount(existing.getTotalCount());
        if (coupon.getValidStart() == null) coupon.setValidStart(existing.getValidStart());
        if (coupon.getValidEnd() == null) coupon.setValidEnd(existing.getValidEnd());
        if (coupon.getStatus() == null) coupon.setStatus(existing.getStatus());
        coupon.setReceivedCount(existing.getReceivedCount());
        int receivedCount = existing.getReceivedCount() == null ? 0 : existing.getReceivedCount();
        if (coupon.getTotalCount() < receivedCount) {
            throw new BusinessException("发行数量不能少于已领取数量");
        }
        couponMapper.updateById(coupon);
        return Result.success("更新成功");
    }

    @Transactional
    public Result<String> deleteAdminCoupon(Long id) {
        if (couponMapper.selectById(id) == null) throw new BusinessException("优惠券不存在");
        couponMapper.deleteById(id);
        return Result.success("删除成功");
    }

    @Transactional
    public Result<String> batchDeleteAdminCoupons(List<Long> ids) {
        if (ids == null || ids.isEmpty()) throw new BusinessException("请选择要删除的优惠券");
        couponMapper.deleteBatchIds(ids);
        return Result.success("批量删除成功");
    }

    public Result<List<Map<String, Object>>> getAdminCouponUsage(Long couponId) {
        if (couponMapper.selectById(couponId) == null) throw new BusinessException("优惠券不存在");
        return Result.success("获取成功", userCouponMapper.selectUsageByCouponId(couponId));
    }

    private void validateCoupon(Coupon coupon, boolean update) {
        if (coupon == null) throw new BusinessException("优惠券参数不能为空");
        if (!update && (coupon.getName() == null || coupon.getName().trim().isEmpty())) throw new BusinessException("请输入优惠券名称");
        if (!update && coupon.getType() == null) throw new BusinessException("请选择优惠券类型");
        if (coupon.getType() != null && (coupon.getType() < 1 || coupon.getType() > 3)) throw new BusinessException("优惠券类型无效");
        if (!update && coupon.getDiscount() == null) throw new BusinessException("请输入优惠金额或折扣率");
        if (coupon.getDiscount() != null && coupon.getDiscount().compareTo(BigDecimal.ZERO) < 0) throw new BusinessException("优惠金额不能为负数");
        if (coupon.getType() != null && coupon.getType() == 2 && coupon.getDiscount() != null
                && coupon.getDiscount().compareTo(BigDecimal.ONE) > 0) {
            throw new BusinessException("折扣券折扣率必须在0到1之间（例如0.8表示8折）");
        }
        if (coupon.getMinAmount() != null && coupon.getMinAmount().compareTo(BigDecimal.ZERO) < 0) throw new BusinessException("使用门槛不能为负数");
        if (!update && coupon.getTotalCount() == null) throw new BusinessException("请输入发行数量");
        if (coupon.getTotalCount() != null && coupon.getTotalCount() <= 0) throw new BusinessException("发行数量必须大于0");
        if (!update && (coupon.getValidStart() == null || coupon.getValidEnd() == null)) throw new BusinessException("请选择优惠券有效期");
        if (coupon.getValidStart() != null && coupon.getValidEnd() != null && coupon.getValidStart().isAfter(coupon.getValidEnd())) throw new BusinessException("有效期开始时间不能晚于结束时间");
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
        
        // 检查用户是否已领取
        LambdaQueryWrapper<UserCoupon> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCoupon::getUserId, userId)
                   .eq(UserCoupon::getCouponId, couponId);
        UserCoupon existUserCoupon = userCouponMapper.selectOne(queryWrapper);
        
        if (existUserCoupon != null) {
            throw new BusinessException("您已领取过该优惠券");
        }
        
        if (couponMapper.incrementReceivedIfAvailable(couponId) != 1) {
            throw new BusinessException("优惠券已被领完");
        }

        // 创建用户优惠券记录；唯一索引兜底防止并发重复领取
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setStatus(0); // 未使用
        int insertResult = userCouponMapper.insert(userCoupon);
        
        log.info("插入用户优惠券记录，结果: {}, ID: {}", insertResult, userCoupon.getId());
        
        log.info("优惠券 {} 领取成功", couponId);
        
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
        if (coupon != null && (coupon.getValidStart() == null || coupon.getValidEnd() == null
                || LocalDateTime.now().isBefore(coupon.getValidStart()) || LocalDateTime.now().isAfter(coupon.getValidEnd()))) {
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

    /** 计算并校验用户优惠券抵扣金额。 */
    public BigDecimal calculateDiscount(Long userId, Long userCouponId, BigDecimal orderAmount) {
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null || !userId.equals(userCoupon.getUserId()) || userCoupon.getStatus() != 0) {
            throw new BusinessException("优惠券不可用");
        }
        Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());
        LocalDateTime now = LocalDateTime.now();
        if (coupon == null || coupon.getStatus() != 1 || coupon.getType() == null || coupon.getType() == 3
                || coupon.getValidStart() == null || coupon.getValidEnd() == null
                || now.isBefore(coupon.getValidStart()) || now.isAfter(coupon.getValidEnd())) {
            throw new BusinessException("优惠券已过期或已失效");
        }
        if (orderAmount == null || coupon.getMinAmount() == null || coupon.getDiscount() == null) {
            throw new BusinessException("优惠券配置无效");
        }
        if (orderAmount.compareTo(coupon.getMinAmount()) < 0) {
            throw new BusinessException("订单金额未达到优惠券使用门槛");
        }
        BigDecimal discount = coupon.getType() == 2
                ? orderAmount.multiply(BigDecimal.ONE.subtract(
                    // 兼容历史数据中以 8 表示 8 折的记录；新建优惠券统一保存 0~1 比例。
                    coupon.getDiscount().compareTo(BigDecimal.ONE) > 0
                            ? coupon.getDiscount().divide(BigDecimal.TEN, 4, java.math.RoundingMode.HALF_UP)
                            : coupon.getDiscount()))
                : coupon.getDiscount();
        return discount.max(BigDecimal.ZERO).min(orderAmount).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    @Transactional
    public void consumeCoupon(Long userCouponId, Long userId, Long orderId) {
        if (userCouponMapper.consumeIfAvailable(userCouponId, userId, orderId) != 1) {
            throw new BusinessException("优惠券已被使用，请重新下单");
        }
    }
}
