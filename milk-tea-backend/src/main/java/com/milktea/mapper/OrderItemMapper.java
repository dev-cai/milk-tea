package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项Mapper
 * @author MilkTea Team
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
