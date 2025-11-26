package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.LoginHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录记录Mapper
 * @author MilkTea Team
 */
@Mapper
public interface LoginHistoryMapper extends BaseMapper<LoginHistory> {
}
