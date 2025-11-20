package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.RefundRequest;
import org.apache.ibatis.annotations.Mapper;

/**
 * 退款申请Mapper
 * @author MilkTea Team
 */
@Mapper
public interface RefundRequestMapper extends BaseMapper<RefundRequest> {
}
