package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单Mapper
 * @author MilkTea Team
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
