package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 用户Mapper - Spring Boot 3
 * @author MilkTea Team
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    int deductPointsIfEnough(@Param("userId") Long userId, @Param("points") Integer points);

    int deductBalanceIfEnough(@org.apache.ibatis.annotations.Param("userId") Long userId,
                              @org.apache.ibatis.annotations.Param("amount") java.math.BigDecimal amount);
    
    /**
     * 查询用户订单统计
     */
    Map<String, Object> selectOrderStats(Long userId);
    
    /**
     * 查询用户邀请统计
     */
    Map<String, Object> selectInviteStats(Long userId);
}
