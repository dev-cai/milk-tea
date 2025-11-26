
-- ========================================
-- 积分系统相关表
-- ========================================

USE `milk_tea`;

-- 先删除已存在的表（如果之前创建失败）
DROP TABLE IF EXISTS `points_history`;
DROP TABLE IF EXISTS `checkin_record`;
DROP TABLE IF EXISTS `invite_record`;

-- 积分历史表
CREATE TABLE IF NOT EXISTS `points_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `points` int(11) NOT NULL COMMENT '积分变动（正数为增加，负数为减少）',
  `type` int(11) NOT NULL COMMENT '类型：1-购物，2-签到，3-邀请，4-评价，5-生日，6-兑换，7-过期',
  `order_id` bigint(20) DEFAULT NULL COMMENT '关联订单ID',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分历史表';

-- 签到记录表
CREATE TABLE IF NOT EXISTS `checkin_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `checkin_date` date NOT NULL COMMENT '签到日期',
  `continuous_days` int(11) DEFAULT '1' COMMENT '连续签到天数',
  `points` int(11) DEFAULT '10' COMMENT '获得积分',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_date` (`user_id`, `checkin_date`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到记录表';

-- 邀请记录表
CREATE TABLE IF NOT EXISTS `invite_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `inviter_id` bigint(20) NOT NULL COMMENT '邀请人ID',
  `invitee_id` bigint(20) NOT NULL COMMENT '被邀请人ID',
  `status` int(11) DEFAULT '0' COMMENT '状态：0-待注册，1-已注册，2-已奖励',
  `points` int(11) DEFAULT '50' COMMENT '奖励积分',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `reward_time` datetime DEFAULT NULL COMMENT '奖励时间',
  PRIMARY KEY (`id`),
  KEY `idx_inviter_id` (`inviter_id`),
  KEY `idx_invitee_id` (`invitee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邀请记录表';

-- 插入测试积分历史数据
INSERT INTO `points_history` (`user_id`, `points`, `type`, `description`, `create_time`) VALUES
(2, 100, 1, '购物消费获得积分', '2024-11-20 10:30:00'),
(2, 10, 2, '每日签到', '2024-11-21 08:00:00'),
(2, 50, 3, '邀请好友注册', '2024-11-22 14:20:00'),
(2, 10, 2, '每日签到', '2024-11-22 08:00:00'),
(2, 5, 4, '订单评价', '2024-11-23 16:30:00');

-- 插入测试签到记录
INSERT INTO `checkin_record` (`user_id`, `checkin_date`, `continuous_days`, `points`) VALUES
(2, '2024-11-21', 1, 10),
(2, '2024-11-22', 2, 10),
(2, '2024-11-23', 3, 10);

-- 插入测试邀请记录
INSERT INTO `invite_record` (`inviter_id`, `invitee_id`, `status`, `points`, `create_time`, `reward_time`) VALUES
(2, 3, 2, 50, '2024-11-22 14:20:00', '2024-11-22 14:21:00');

-- 创建存储过程前先删除已存在的
DROP PROCEDURE IF EXISTS `update_member_level`;

DELIMITER $$
CREATE PROCEDURE update_member_level(IN p_user_id BIGINT)
BEGIN
    DECLARE v_points INT;
    DECLARE v_new_level INT;
    
    -- 获取用户当前积分
    SELECT points INTO v_points FROM user WHERE id = p_user_id;
    
    -- 根据积分计算会员等级
    IF v_points >= 5000 THEN
        SET v_new_level = 2; -- 钻石会员
    ELSEIF v_points >= 1000 THEN
        SET v_new_level = 1; -- 黄金会员
    ELSE
        SET v_new_level = 0; -- 普通会员
    END IF;
    
    -- 更新会员等级
    UPDATE user SET member_level = v_new_level WHERE id = p_user_id;
END $$
DELIMITER ;

-- 为现有用户更新会员等级
CALL update_member_level(2);
CALL update_member_level(3);

