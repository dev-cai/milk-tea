package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.PointsProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 积分商品Mapper
 */
@Mapper
public interface PointsProductMapper extends BaseMapper<PointsProduct> {
    @Update("UPDATE points_product SET stock = stock - #{quantity}, exchanged = exchanged + #{quantity} WHERE id = #{productId} AND status = 1 AND deleted = 0 AND stock >= #{quantity}")
    int decrementStockIfAvailable(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}
