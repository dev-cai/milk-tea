package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.RefundRequest;
import org.apache.ibatis.annotations.Mapper;

/**
 * 退款申请Mapper
 */
@Mapper
public interface RefundRequestMapper extends BaseMapper<RefundRequest> {
    int processIfPending(@org.apache.ibatis.annotations.Param("id") Long id, @org.apache.ibatis.annotations.Param("status") Integer status, @org.apache.ibatis.annotations.Param("rejectReason") String rejectReason);
}
