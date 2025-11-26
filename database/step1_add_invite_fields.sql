-- 步骤1: 添加邀请相关字段
USE milk_tea;

-- 添加邀请字段
ALTER TABLE `user` 
ADD COLUMN `invite_code` VARCHAR(20) DEFAULT NULL COMMENT '邀请码' AFTER `member_no`,
ADD COLUMN `inviter_id` BIGINT(20) DEFAULT NULL COMMENT '邀请人ID' AFTER `invite_code`,
ADD COLUMN `invite_time` DATETIME DEFAULT NULL COMMENT '被邀请时间' AFTER `inviter_id`;

-- 添加索引
ALTER TABLE `user` 
ADD UNIQUE KEY `uk_invite_code` (`invite_code`),
ADD KEY `idx_inviter_id` (`inviter_id`);

-- 查看表结构
DESC `user`;
