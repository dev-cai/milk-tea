package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品分类Mapper
 * @author MilkTea Team
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
