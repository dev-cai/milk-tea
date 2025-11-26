-- ========================================
-- 创建登录记录表
-- ========================================

USE `milk_tea`;

-- 创建登录记录表
DROP TABLE IF EXISTS `login_history`;
CREATE TABLE `login_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `device` varchar(100) DEFAULT NULL COMMENT '登录设备',
  `device_type` varchar(50) DEFAULT NULL COMMENT '设备类型：ios/android/web/miniapp',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `location` varchar(200) DEFAULT NULL COMMENT '登录地点',
  `login_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_login_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录记录表';

-- 插入一些测试数据
INSERT INTO `login_history` (`user_id`, `device`, `device_type`, `ip`, `location`, `login_time`) VALUES
(2, 'iPhone 13', 'ios', '192.168.1.100', '广东省深圳市', '2024-11-26 14:30:25'),
(2, 'Android', 'android', '192.168.1.101', '广东省深圳市', '2024-11-25 09:15:10'),
(2, '微信小程序', 'miniapp', '192.168.1.102', '广东省深圳市', '2024-11-24 20:45:33'),
(3, 'iPhone 12', 'ios', '192.168.1.103', '北京市朝阳区', '2024-11-26 10:20:15'),
(3, 'Web浏览器', 'web', '192.168.1.104', '北京市朝阳区', '2024-11-25 16:30:00');
