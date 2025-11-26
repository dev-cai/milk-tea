-- 创建用户设置表
USE `milk_tea`;

CREATE TABLE IF NOT EXISTS `user_settings` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `order_notification` INT DEFAULT 1 COMMENT '订单通知：0-关闭 1-开启',
  `activity_push` INT DEFAULT 1 COMMENT '活动推送：0-关闭 1-开启',
  `coupon_reminder` INT DEFAULT 1 COMMENT '优惠提醒：0-关闭 1-开启',
  `personalized_recommend` INT DEFAULT 1 COMMENT '个性化推荐：0-关闭 1-开启',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户设置表';
