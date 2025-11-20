package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.PrintDevice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 打印设备Mapper
 * @author MilkTea Team
 */
@Mapper
public interface PrintDeviceMapper extends BaseMapper<PrintDevice> {
}
