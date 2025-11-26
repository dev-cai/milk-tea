package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.ProductCollection;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品收藏Mapper
 */
@Mapper
public interface ProductCollectionMapper extends BaseMapper<ProductCollection> {
}
