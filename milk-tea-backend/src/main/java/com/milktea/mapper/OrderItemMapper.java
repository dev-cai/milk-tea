package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 订单项Mapper
 * @author MilkTea Team
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
    
    /**
     * 查询用户购买过的商品ID列表（去重）
     * 只查询已完成的订单
     */
    @Select("SELECT DISTINCT oi.product_id " +
            "FROM order_item oi " +
            "INNER JOIN orders o ON oi.order_id = o.id " +
            "WHERE o.user_id = #{userId} " +
            "AND o.status IN (3, 4) " +
            "AND o.deleted = 0 " +
            "ORDER BY oi.create_time DESC")
    List<Long> selectPurchasedProductIdsByUserId(Long userId);

    @Select("SELECT oi.product_id AS productId, p.category_id AS categoryId, SUM(oi.quantity) AS quantity, " +
            "SUM(oi.quantity * oi.price) AS amount, MAX(o.create_time) AS lastBuy " +
            "FROM order_item oi JOIN orders o ON o.id = oi.order_id JOIN product p ON p.id = oi.product_id " +
            "WHERE o.user_id = #{userId} AND o.status IN (3,4) AND o.deleted = 0 " +
            "GROUP BY oi.product_id, p.category_id")
    List<Map<String, Object>> selectPurchaseStatsByUserId(Long userId);
}
