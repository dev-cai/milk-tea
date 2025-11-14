package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户地址Mapper
 * @author MilkTea Team
 */
@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddress> {
}
