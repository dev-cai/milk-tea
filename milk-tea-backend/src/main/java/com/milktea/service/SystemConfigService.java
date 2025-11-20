package com.milktea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.milktea.entity.SystemConfig;
import com.milktea.mapper.SystemConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统配置服务类
 */
@Service
public class SystemConfigService {

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    /**
     * 获取所有配置
     */
    public List<SystemConfig> getAllConfigs() {
        return systemConfigMapper.selectList(null);
    }

    /**
     * 获取配置映射（key-value）
     */
    public Map<String, String> getConfigMap() {
        List<SystemConfig> configs = systemConfigMapper.selectList(null);
        Map<String, String> configMap = new HashMap<>();
        for (SystemConfig config : configs) {
            configMap.put(config.getConfigKey(), config.getConfigValue());
        }
        return configMap;
    }

    /**
     * 根据key获取配置值
     */
    public String getConfigValue(String key) {
        SystemConfig config = systemConfigMapper.selectOne(
            new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getConfigKey, key)
        );
        return config != null ? config.getConfigValue() : null;
    }

    /**
     * 根据key获取配置值（带默认值）
     */
    public String getConfigValue(String key, String defaultValue) {
        String value = getConfigValue(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取布尔类型配置
     */
    public boolean getBooleanConfig(String key, boolean defaultValue) {
        String value = getConfigValue(key);
        if (value == null) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(value) || "1".equals(value);
    }

    /**
     * 获取整数类型配置
     */
    public int getIntConfig(String key, int defaultValue) {
        String value = getConfigValue(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 获取浮点类型配置
     */
    public double getDoubleConfig(String key, double defaultValue) {
        String value = getConfigValue(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 更新配置
     */
    public boolean updateConfig(String key, String value) {
        SystemConfig config = systemConfigMapper.selectOne(
            new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getConfigKey, key)
        );
        
        if (config == null) {
            // 如果配置不存在，创建新配置
            config = new SystemConfig();
            config.setConfigKey(key);
            config.setConfigValue(value);
            config.setType(1); // 默认文本类型
            return systemConfigMapper.insert(config) > 0;
        } else {
            config.setConfigValue(value);
            return systemConfigMapper.updateById(config) > 0;
        }
    }

    /**
     * 批量更新配置
     */
    public boolean batchUpdateConfig(Map<String, String> configMap) {
        for (Map.Entry<String, String> entry : configMap.entrySet()) {
            updateConfig(entry.getKey(), entry.getValue());
        }
        return true;
    }

    /**
     * 删除配置
     */
    public boolean deleteConfig(String key) {
        return systemConfigMapper.delete(
            new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getConfigKey, key)
        ) > 0;
    }

    /**
     * 更新最后备份时间
     */
    public void updateLastBackupTime() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        updateConfig("system_last_backup", now);
    }

    /**
     * 获取最后备份时间
     */
    public String getLastBackupTime() {
        return getConfigValue("system_last_backup", "");
    }
}
