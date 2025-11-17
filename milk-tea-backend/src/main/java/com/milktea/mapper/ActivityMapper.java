package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.Activity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 营销活动Mapper接口
 */
@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {
}
