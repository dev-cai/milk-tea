-- Milk Tea database bootstrap script.
-- Run from the repository root with: mysql -u root -p milk_tea < database/init.sql
-- The dump contains the canonical schema and seed data. The statements below
-- add production-hardening fields that are intentionally kept out of the dump.
SOURCE database/milk_tea-test-bak.sql;

USE `milk_tea`;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

ALTER TABLE `user`
  ADD COLUMN `token_version` INT NOT NULL DEFAULT 0 COMMENT '令牌版本';

ALTER TABLE `orders`
  ADD INDEX `idx_user_status_time` (`user_id`, `status`, `create_time`),
  ADD INDEX `idx_status_time` (`status`, `create_time`);

ALTER TABLE `product`
  ADD INDEX `idx_cate_status_sales` (`category_id`, `status`, `sales`),
  ADD INDEX `idx_status_sales` (`status`, `sales`);

ALTER TABLE `refund_request` ADD INDEX `idx_refund_status` (`status`);
ALTER TABLE `complaint` ADD INDEX `idx_complaint_status` (`status`);
ALTER TABLE `feedback` ADD INDEX `idx_feedback_status` (`status`);

-- 清理历史重复领券记录后再建立唯一约束，避免初始化时因脏数据失败。
DELETE FROM `user_coupon`
WHERE `id` NOT IN (
  SELECT `id` FROM (
    SELECT MIN(`id`) AS `id`
    FROM `user_coupon`
    GROUP BY `user_id`, `coupon_id`
  ) AS keep_rows
);

ALTER TABLE `user_coupon` ADD UNIQUE KEY `uk_user_coupon` (`user_id`, `coupon_id`);

-- Public activity endpoints use this lightweight activity model.
CREATE TABLE IF NOT EXISTS `activity` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `type` INT DEFAULT NULL,
  `description` VARCHAR(1000) DEFAULT NULL,
  `rules` VARCHAR(2000) DEFAULT NULL,
  `discount` DECIMAL(10,2) DEFAULT NULL,
  `min_amount` DECIMAL(10,2) DEFAULT NULL,
  `start_time` DATETIME DEFAULT NULL,
  `end_time` DATETIME DEFAULT NULL,
  `status` INT NOT NULL DEFAULT 1,
  `participant_count` INT NOT NULL DEFAULT 0,
  `deleted` INT NOT NULL DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_activity_status_time` (`status`, `start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
