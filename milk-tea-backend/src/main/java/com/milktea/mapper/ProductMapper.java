package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品Mapper - Spring Boot 3
 * @author MilkTea Team
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
