package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

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
    List<Long> selectPurchasedProductIdsByUserId(Long userId);

    List<Map<String, Object>> selectPurchaseStatsByUserId(Long userId);
}
