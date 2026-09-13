package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券Mapper
 * @author MilkTea Team
 */
@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {
    int incrementReceivedIfAvailable(Long couponId);
}
