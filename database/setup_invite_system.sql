-- 邀请系统数据库设置
USE milk_tea;

-- 检查user表是否已有invite_code字段
SELECT COUNT(*) as has_column 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = 'milk_tea' 
  AND TABLE_NAME = 'user' 
  AND COLUMN_NAME = 'invite_code';


USE milk_tea;

ALTER TABLE `user` 
ADD COLUMN `invite_code` VARCHAR(20) DEFAULT NULL COMMENT '邀请码' AFTER `member_no`,
ADD COLUMN `inviter_id` BIGINT(20) DEFAULT NULL COMMENT '邀请人ID' AFTER `invite_code`,
ADD COLUMN `invite_time` DATETIME DEFAULT NULL COMMENT '被邀请时间' AFTER `inviter_id`;

ALTER TABLE `user` 
ADD UNIQUE KEY `uk_invite_code` (`invite_code`),
ADD KEY `idx_inviter_id` (`inviter_id`);

-- 为现有用户生成邀请码（只更新没有邀请码的用户）
UPDATE `user` 
SET `invite_code` = CONCAT(
    SUBSTRING('ABCDEFGHJKLMNPQRSTUVWXYZ23456789', FLOOR(1 + RAND() * 32), 1),
    SUBSTRING('ABCDEFGHJKLMNPQRSTUVWXYZ23456789', FLOOR(1 + RAND() * 32), 1),
    SUBSTRING('ABCDEFGHJKLMNPQRSTUVWXYZ23456789', FLOOR(1 + RAND() * 32), 1),
    SUBSTRING('ABCDEFGHJKLMNPQRSTUVWXYZ23456789', FLOOR(1 + RAND() * 32), 1),
    SUBSTRING('ABCDEFGHJKLMNPQRSTUVWXYZ23456789', FLOOR(1 + RAND() * 32), 1),
    SUBSTRING('ABCDEFGHJKLMNPQRSTUVWXYZ23456789', FLOOR(1 + RAND() * 32), 1)
)
WHERE `invite_code` IS NULL OR `invite_code` = '';

-- 查看结果
SELECT id, username, nickname, invite_code, inviter_id FROM `user` LIMIT 10;
