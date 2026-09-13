package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Select;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单Mapper
 * @author MilkTea Team
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    @Update("UPDATE orders SET status = #{targetStatus} WHERE id = #{orderId} AND user_id = #{userId} AND status = #{expectedStatus}")
    int updateStatusIf(@Param("orderId") Long orderId, @Param("userId") Long userId,
                       @Param("expectedStatus") Integer expectedStatus, @Param("targetStatus") Integer targetStatus);

    @Update("UPDATE orders SET status = #{targetStatus} WHERE id = #{orderId} AND status = #{expectedStatus}")
    int updateStatusIfExpected(@Param("orderId") Long orderId,
                               @Param("expectedStatus") Integer expectedStatus,
                               @Param("targetStatus") Integer targetStatus);

    @Update("UPDATE orders SET status = 1, pay_time = NOW(), pay_amount = #{amount}, actual_amount = #{amount} " +
            "WHERE id = #{orderId} AND user_id = #{userId} AND status = 0")
    int markPaidIfPending(@Param("orderId") Long orderId, @Param("userId") Long userId,
                          @Param("amount") java.math.BigDecimal amount);

    @Select("SELECT DATE(create_time) AS day, COUNT(*) AS orderCount, COALESCE(SUM(pay_amount), 0) AS amount " +
            "FROM orders WHERE status = 4 AND create_time >= #{start} AND deleted = 0 " +
            "GROUP BY DATE(create_time) ORDER BY day")
    List<Map<String, Object>> selectSalesTrend(@Param("start") LocalDateTime start);

    @Select("SELECT oi.product_id AS productId, MAX(oi.product_name) AS productName, " +
            "SUM(oi.quantity) AS quantity, COALESCE(SUM(oi.price * oi.quantity), 0) AS revenue " +
            "FROM order_item oi JOIN orders o ON o.id = oi.order_id " +
            "WHERE o.status = 4 AND o.deleted = 0 GROUP BY oi.product_id ORDER BY quantity DESC LIMIT #{limit}")
    List<Map<String, Object>> selectProductRanking(@Param("limit") Integer limit);
}
