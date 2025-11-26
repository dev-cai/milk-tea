-- ========================================
-- 为用户表添加会员号字段
-- ========================================

USE `milk_tea`;

-- 添加会员号字段
ALTER TABLE `user` ADD COLUMN `member_no` varchar(50) DEFAULT NULL COMMENT '会员号' AFTER `member_level`;

-- 为现有用户生成会员号（格式：MT + 年月日 + 6位随机数）
-- 使用用户ID作为基础生成唯一会员号
UPDATE `user` SET `member_no` = CONCAT('MT', DATE_FORMAT(create_time, '%Y%m%d'), LPAD(id, 6, '0')) WHERE `member_no` IS NULL;

-- 为会员号字段添加唯一索引
ALTER TABLE `user` ADD UNIQUE KEY `uk_member_no` (`member_no`);
