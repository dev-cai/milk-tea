package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper - Spring Boot 3
 * @author MilkTea Team
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
