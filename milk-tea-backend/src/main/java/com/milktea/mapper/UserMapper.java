package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Map;

/**
 * 用户Mapper - Spring Boot 3
 * @author MilkTea Team
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Update("UPDATE user SET points = points - #{points} WHERE id = #{userId} AND points >= #{points} AND deleted = 0")
    int deductPointsIfEnough(@Param("userId") Long userId, @Param("points") Integer points);

    @Update("UPDATE user SET balance = balance - #{amount} WHERE id = #{userId} AND balance >= #{amount} AND status = 1 AND deleted = 0")
    int deductBalanceIfEnough(@org.apache.ibatis.annotations.Param("userId") Long userId,
                              @org.apache.ibatis.annotations.Param("amount") java.math.BigDecimal amount);
    
    /**
     * 查询用户订单统计
     */
    @Select("SELECT COUNT(*) as totalOrders, COALESCE(SUM(pay_amount), 0) as totalAmount " +
            "FROM orders WHERE user_id = #{userId} AND status IN (1,2,3,4)")
    Map<String, Object> selectOrderStats(Long userId);
    
    /**
     * 查询用户邀请统计
     */
    @Select("SELECT COUNT(*) as inviteCount FROM invite_record " +
            "WHERE inviter_id = #{userId} AND status = 2")
    Map<String, Object> selectInviteStats(Long userId);
}
