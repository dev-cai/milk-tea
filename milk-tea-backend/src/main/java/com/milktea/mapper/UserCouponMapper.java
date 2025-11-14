package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.UserCoupon;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户优惠券Mapper
 * @author MilkTea Team
 */
@Mapper
public interface UserCouponMapper extends BaseMapper<UserCoupon> {
}
