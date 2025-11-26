-- ========================================
-- 为用户表添加性别和生日字段
-- ========================================

USE `milk_tea`;

-- 添加性别字段 (1-男, 2-女)
ALTER TABLE `user` ADD COLUMN `gender` int(11) DEFAULT NULL COMMENT '性别：1-男，2-女' AFTER `phone`;

-- 添加生日字段
ALTER TABLE `user` ADD COLUMN `birthday` varchar(20) DEFAULT NULL COMMENT '生日' AFTER `gender`;

-- 更新现有测试用户的性别和生日数据
UPDATE `user` SET `gender` = 1, `birthday` = '1990-01-15' WHERE `username` = 'user1';
UPDATE `user` SET `gender` = 2, `birthday` = '1992-05-20' WHERE `username` = 'user2';
UPDATE `user` SET `gender` = 1, `birthday` = '1988-08-10' WHERE `username` = 'admin';
