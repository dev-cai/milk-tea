package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.PointsHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 积分历史Mapper
 * @author MilkTea Team
 */
@Mapper
public interface PointsHistoryMapper extends BaseMapper<PointsHistory> {
}
