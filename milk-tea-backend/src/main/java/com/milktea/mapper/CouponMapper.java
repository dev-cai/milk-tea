package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * 优惠券Mapper
 * @author MilkTea Team
 */
@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {

    @Update("UPDATE coupon SET received_count = COALESCE(received_count, 0) + 1 " +
            "WHERE id = #{couponId} AND status = 1 AND received_count < total_count AND deleted = 0")
    int incrementReceivedIfAvailable(Long couponId);
}
