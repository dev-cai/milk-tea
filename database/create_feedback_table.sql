-- 使用数据库
USE `milk_tea`;

-- 创建反馈表
CREATE TABLE IF NOT EXISTS `feedback` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
  `type` INT NOT NULL COMMENT '反馈类型：1-产品问题 2-服务问题 3-配送问题 4-支付问题 5-功能建议 6-其他问题',
  `content` TEXT NOT NULL COMMENT '反馈内容',
  `images` VARCHAR(500) DEFAULT NULL COMMENT '图片地址，多张图片用逗号分隔',
  `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `contact_email` VARCHAR(100) DEFAULT NULL COMMENT '联系邮箱',
  `status` INT DEFAULT 0 COMMENT '处理状态：0-待处理 1-处理中 2-已回复',
  `reply` TEXT DEFAULT NULL COMMENT '客服回复内容',
  `reply_time` DATETIME DEFAULT NULL COMMENT '回复时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户反馈表';
