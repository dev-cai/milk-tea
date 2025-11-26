package com.milktea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.milktea.entity.UserSettings;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户设置Mapper
 */
@Mapper
public interface UserSettingsMapper extends BaseMapper<UserSettings> {
}
