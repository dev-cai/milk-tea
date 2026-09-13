package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.RefundRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * 退款申请Mapper
 */
@Mapper
public interface RefundRequestMapper extends BaseMapper<RefundRequest> {
    @Update("UPDATE refund_request SET status = #{status}, process_time = NOW(), reject_reason = #{rejectReason} " +
            "WHERE id = #{id} AND status = 0")
    int processIfPending(@org.apache.ibatis.annotations.Param("id") Long id,
                         @org.apache.ibatis.annotations.Param("status") Integer status,
                         @org.apache.ibatis.annotations.Param("rejectReason") String rejectReason);
}
