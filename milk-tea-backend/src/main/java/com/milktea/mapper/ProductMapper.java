package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * 商品Mapper - Spring Boot 3
 * @author MilkTea Team
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    @Update("UPDATE product SET stock = stock - #{quantity} WHERE id = #{productId} AND status = 1 AND deleted = 0 AND stock >= #{quantity}")
    int decrementStock(@org.apache.ibatis.annotations.Param("productId") Long productId,
                       @org.apache.ibatis.annotations.Param("quantity") Integer quantity);
}
