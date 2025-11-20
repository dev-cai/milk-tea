package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.Complaint;
import org.apache.ibatis.annotations.Mapper;

/**
 * 投诉Mapper
 * @author MilkTea Team
 */
@Mapper
public interface ComplaintMapper extends BaseMapper<Complaint> {
}
