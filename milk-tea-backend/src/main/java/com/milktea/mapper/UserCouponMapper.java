package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.dto.UserCouponDTO;
import com.milktea.entity.UserCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户优惠券Mapper
 * @author MilkTea Team
 */
@Mapper
public interface UserCouponMapper extends BaseMapper<UserCoupon> {

    List<Map<String, Object>> selectUsageByCouponId(@Param("couponId") Long couponId);

    int consumeIfAvailable(@Param("userCouponId") Long userCouponId, @Param("userId") Long userId, @Param("orderId") Long orderId);
    
    /**
     * 查询用户优惠券列表（包含优惠券详情）
     */
    List<UserCouponDTO> selectUserCouponsWithDetail(@Param("userId") Long userId, @Param("status") Integer status);
}
