package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.Staff;
import org.apache.ibatis.annotations.Mapper;

/**
 * 员工Mapper接口
 */
@Mapper
public interface StaffMapper extends BaseMapper<Staff> {
}
