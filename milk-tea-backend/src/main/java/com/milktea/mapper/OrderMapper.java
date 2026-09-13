package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单Mapper
 * @author MilkTea Team
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    int updateStatusIf(@Param("orderId") Long orderId, @Param("userId") Long userId, @Param("expectedStatus") Integer expectedStatus, @Param("targetStatus") Integer targetStatus);
    int updateStatusIfExpected(@Param("orderId") Long orderId, @Param("expectedStatus") Integer expectedStatus, @Param("targetStatus") Integer targetStatus);
    int markPaidIfPending(@Param("orderId") Long orderId, @Param("userId") Long userId, @Param("amount") java.math.BigDecimal amount);
    List<Map<String, Object>> selectSalesTrend(@Param("start") LocalDateTime start);
    List<Map<String, Object>> selectProductRanking(@Param("limit") Integer limit);
}
