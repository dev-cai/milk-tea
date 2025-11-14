package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.Recipe;
import org.apache.ibatis.annotations.Mapper;

/**
 * 配方Mapper
 * @author MilkTea Team
 */
@Mapper
public interface RecipeMapper extends BaseMapper<Recipe> {
}
