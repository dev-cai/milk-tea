package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.dto.UserCouponDTO;
import com.milktea.entity.UserCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户优惠券Mapper
 * @author MilkTea Team
 */
@Mapper
public interface UserCouponMapper extends BaseMapper<UserCoupon> {
    
    /**
     * 查询用户优惠券列表（包含优惠券详情）
     */
    @Select("<script>" +
            "SELECT " +
            "uc.id, " +
            "uc.user_id as userId, " +
            "uc.coupon_id as couponId, " +
            "uc.status, " +
            "uc.order_id as orderId, " +
            "uc.use_time as useTime, " +
            "uc.create_time as createTime, " +
            "c.name, " +
            "c.type, " +
            "c.discount, " +
            "c.min_amount as minAmount, " +
            "c.valid_start as startTime, " +
            "c.valid_end as endTime, " +
            "CONCAT('满', c.min_amount, '元可用') as description " +
            "FROM user_coupon uc " +
            "LEFT JOIN coupon c ON uc.coupon_id = c.id " +
            "WHERE uc.user_id = #{userId} " +
            "<if test='status != null'>" +
            "AND uc.status = #{status} " +
            "</if>" +
            "ORDER BY uc.create_time DESC" +
            "</script>")
    List<UserCouponDTO> selectUserCouponsWithDetail(@Param("userId") Long userId, @Param("status") Integer status);
}
