package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.PointsProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 积分商品Mapper
 */
@Mapper
public interface PointsProductMapper extends BaseMapper<PointsProduct> {
    int decrementStockIfAvailable(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}
