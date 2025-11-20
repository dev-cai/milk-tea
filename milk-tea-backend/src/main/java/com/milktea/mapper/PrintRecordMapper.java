package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.PrintRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 打印记录Mapper
 * @author MilkTea Team
 */
@Mapper
public interface PrintRecordMapper extends BaseMapper<PrintRecord> {
}
