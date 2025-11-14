-- ========================================
-- 奶茶小程序数据库设计
-- 数据库名：milk_tea
-- 作者：MilkTea Team
-- 创建时间：2025-11-13
-- ========================================

CREATE DATABASE IF NOT EXISTS `milk_tea` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `milk_tea`;

-- ========================================
-- 用户表
-- ========================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码（MD5加密）',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `openid` varchar(100) DEFAULT NULL COMMENT '微信openid',
  `member_level` int(11) DEFAULT '0' COMMENT '会员等级：0-普通会员，1-黄金会员，2-钻石会员',
  `points` int(11) DEFAULT '0' COMMENT '积分',
  `balance` decimal(10,2) DEFAULT '0.00' COMMENT '余额',
  `user_type` int(11) DEFAULT '0' COMMENT '用户类型：0-普通用户，1-管理员',
  `status` int(11) DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_phone` (`phone`),
  KEY `idx_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ========================================
-- 商品分类表
-- ========================================
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父级分类ID，0表示一级分类',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `icon` varchar(255) DEFAULT NULL COMMENT '分类图标',
  `status` int(11) DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ========================================
-- 商品表
-- ========================================
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name` varchar(100) NOT NULL COMMENT '商品名称',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `description` text COMMENT '商品描述',
  `image` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `member_price` decimal(10,2) DEFAULT NULL COMMENT '会员价格',
  `cost` decimal(10,2) DEFAULT NULL COMMENT '成本价格',
  `stock` int(11) DEFAULT '0' COMMENT '库存',
  `sales` int(11) DEFAULT '0' COMMENT '销量',
  `nutrition` text COMMENT '营养成分',
  `status` int(11) DEFAULT '1' COMMENT '状态：0-下架，1-上架',
  `is_recommend` int(11) DEFAULT '0' COMMENT '是否推荐：0-否，1-是',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ========================================
-- 订单表
-- ========================================
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
  `discount_amount` decimal(10,2) DEFAULT '0.00' COMMENT '优惠金额',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '实付金额',
  `pay_type` int(11) DEFAULT NULL COMMENT '支付方式：1-微信支付，2-余额支付，3-组合支付',
  `status` int(11) DEFAULT '0' COMMENT '订单状态：0-待支付，1-待制作，2-制作中，3-待取餐，4-已完成，5-已取消，6-退款中，7-已退款',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `refund_reason` varchar(255) DEFAULT NULL COMMENT '退款原因',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ========================================
-- 订单明细表
-- ========================================
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单明细ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) NOT NULL COMMENT '商品名称',
  `product_image` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `price` decimal(10,2) NOT NULL COMMENT '单价',
  `quantity` int(11) NOT NULL COMMENT '数量',
  `sweetness` int(11) DEFAULT NULL COMMENT '甜度：0-无糖，1-三分，2-五分，3-七分，4-正常',
  `temperature` int(11) DEFAULT NULL COMMENT '温度：0-去冰，1-少冰，2-正常，3-热',
  `toppings` varchar(255) DEFAULT NULL COMMENT '加料（JSON格式）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- ========================================
-- 用户地址表
-- ========================================
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `name` varchar(50) NOT NULL COMMENT '收货人',
  `phone` varchar(20) NOT NULL COMMENT '联系电话',
  `province` varchar(50) DEFAULT NULL COMMENT '省份',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `district` varchar(50) DEFAULT NULL COMMENT '区县',
  `detail` varchar(255) NOT NULL COMMENT '详细地址',
  `tag` int(11) DEFAULT NULL COMMENT '地址标签：1-家庭，2-公司，3-学校，4-其他',
  `is_default` int(11) DEFAULT '0' COMMENT '是否默认：0-否，1-是',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表';

-- ========================================
-- 优惠券表
-- ========================================
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
  `name` varchar(100) NOT NULL COMMENT '优惠券名称',
  `type` int(11) NOT NULL COMMENT '优惠券类型：1-满减券，2-折扣券，3-兑换券',
  `discount` decimal(10,2) NOT NULL COMMENT '折扣金额或折扣率',
  `min_amount` decimal(10,2) DEFAULT '0.00' COMMENT '最低消费金额',
  `total_count` int(11) NOT NULL COMMENT '发行数量',
  `received_count` int(11) DEFAULT '0' COMMENT '已领取数量',
  `valid_start` datetime NOT NULL COMMENT '有效期开始',
  `valid_end` datetime NOT NULL COMMENT '有效期结束',
  `status` int(11) DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';

-- ========================================
-- 用户优惠券表
-- ========================================
DROP TABLE IF EXISTS `user_coupon`;
CREATE TABLE `user_coupon` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户优惠券ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `coupon_id` bigint(20) NOT NULL COMMENT '优惠券ID',
  `status` int(11) DEFAULT '0' COMMENT '状态：0-未使用，1-已使用，2-已过期',
  `order_id` bigint(20) DEFAULT NULL COMMENT '使用的订单ID',
  `use_time` datetime DEFAULT NULL COMMENT '使用时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_coupon_id` (`coupon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';

-- ========================================
-- 轮播图表
-- ========================================
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `title` varchar(100) DEFAULT NULL COMMENT '标题',
  `image` varchar(255) NOT NULL COMMENT '图片地址',
  `link` varchar(255) DEFAULT NULL COMMENT '跳转链接',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `status` int(11) DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

-- ========================================
-- 评价表
-- ========================================
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `rating` int(11) NOT NULL COMMENT '评分：1-5星',
  `content` text COMMENT '评价内容',
  `images` varchar(500) DEFAULT NULL COMMENT '图片（JSON格式）',
  `is_anonymous` int(11) DEFAULT '0' COMMENT '是否匿名：0-否，1-是',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- ========================================
-- 初始化数据
-- ========================================

-- 插入管理员账户（用户名：admin，密码：admin123 - MD5加密）
INSERT INTO `user` (`username`, `password`, `nickname`, `user_type`, `status`) 
VALUES ('admin', '0192023a7bbd73250516f069df18b500', '系统管理员', 1, 1);

-- 插入测试用户（用户名：user1，密码：123456 - MD5加密）
INSERT INTO `user` (`username`, `password`, `nickname`, `phone`, `member_level`, `points`, `balance`) 
VALUES ('user1', 'e10adc3949ba59abbe56e057f20f883e', '张三', '13800138000', 0, 100, 50.00);

-- 插入测试用户2（用户名：user2，密码：123456 - MD5加密）
INSERT INTO `user` (`username`, `password`, `nickname`, `phone`, `member_level`, `points`, `balance`) 
VALUES ('user2', 'e10adc3949ba59abbe56e057f20f883e', '李四', '13900139000', 1, 500, 100.00);

-- 插入商品分类
INSERT INTO `category` (`name`, `parent_id`, `sort`) VALUES
('茶饮', 0, 1),
('咖啡', 0, 2),
('小食', 0, 3),
('奶茶', 1, 1),
('果茶', 1, 2),
('奶盖茶', 1, 3);

-- 插入商品
INSERT INTO `product` (`name`, `category_id`, `description`, `image`, `price`, `member_price`, `cost`, `stock`, `sales`, `nutrition`, `status`, `is_recommend`, `sort`) VALUES
('珍珠奶茶', 1, '经典珍珠奶茶，香浓可口', 'product1.jpg', 12.00, 10.80, 8.00, 100, 156, '热量：200kcal', 1, 1, 1),
('波霸奶茶', 1, '大颗粒珍珠，Q弹有嚼劲', 'product2.jpg', 13.00, 11.70, 8.50, 100, 98, '热量：210kcal', 1, 1, 2),
('红豆奶茶', 1, '香甜红豆配奶茶', 'product3.jpg', 14.00, 12.60, 9.00, 100, 78, '热量：220kcal', 1, 0, 3),
('芋泥奶茶', 1, '浓郁芋泥，香甜可口', 'product4.jpg', 15.00, 13.50, 10.00, 100, 145, '热量：230kcal', 1, 1, 4),
('百香果茶', 2, '清新百香果，酸甜可口', 'product5.jpg', 16.00, 14.40, 10.50, 100, 112, '热量：150kcal', 1, 1, 5),
('柠檬茶', 2, '新鲜柠檬，清爽解渴', 'product6.jpg', 14.00, 12.60, 9.00, 100, 89, '热量：120kcal', 1, 0, 6),
('芝士奶盖茶', 2, '浓郁芝士奶盖', 'product7.jpg', 18.00, 16.20, 12.00, 100, 134, '热量：280kcal', 1, 1, 7),
('美式咖啡', 2, '经典美式咖啡', 'product8.jpg', 15.00, 13.50, 10.00, 100, 67, '热量：5kcal', 1, 0, 8),
('拿铁咖啡', 2, '香浓拿铁', 'product9.jpg', 18.00, 16.20, 12.00, 100, 92, '热量：180kcal', 1, 0, 9),
('芝士蛋糕', 3, '香滑芝士蛋糕', 'product10.jpg', 25.00, 22.50, 15.00, 50, 56, '热量：350kcal', 1, 1, 10);

-- 插入优惠券
INSERT INTO `coupon` (`name`, `type`, `discount`, `min_amount`, `total_count`, `valid_start`, `valid_end`, `status`) VALUES
('新用户专享券', 1, 5.00, 20.00, 1000, '2024-01-01 00:00:00', '2025-12-31 23:59:59', 1),
('满30减10', 1, 10.00, 30.00, 500, '2024-01-01 00:00:00', '2025-12-31 23:59:59', 1),
('8折优惠券', 2, 0.80, 0.00, 300, '2024-01-01 00:00:00', '2025-12-31 23:59:59', 1);

-- 插入轮播图
INSERT INTO `banner` (`title`, `image`, `sort`, `status`) VALUES
('新品上市', 'https://via.placeholder.com/750x300?text=New+Products', 1, 1),
('限时特惠', 'https://via.placeholder.com/750x300?text=Special+Offer', 2, 1),
('会员专享', 'https://via.placeholder.com/750x300?text=Member+Exclusive', 3, 1);

-- 插入用户地址
INSERT INTO `user_address` (`user_id`, `name`, `phone`, `province`, `city`, `district`, `detail`, `tag`, `is_default`) VALUES
(2, '张三', '13800138000', '广东省', '深圳市', '南山区', '科技园南区', 1, 1),
(2, '张三', '13800138000', '广东省', '深圳市', '福田区', '华强北商圈', 2, 0),
(3, '李四', '13900139000', '北京市', '北京市', '朝阳区', '三里屯', 1, 1);

-- 插入用户优惠券
INSERT INTO `user_coupon` (`user_id`, `coupon_id`, `status`) VALUES
(2, 1, 0),
(2, 2, 0),
(3, 1, 0),
(3, 3, 1);

-- ========================================
-- 商品配方表
-- ========================================
DROP TABLE IF EXISTS `recipe`;
CREATE TABLE `recipe` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配方ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `ingredient_name` varchar(100) NOT NULL COMMENT '原料名称',
  `quantity` decimal(10,2) NOT NULL COMMENT '用量',
  `unit` varchar(20) NOT NULL COMMENT '单位',
  `unit_cost` decimal(10,2) NOT NULL COMMENT '单价',
  `total_cost` decimal(10,2) NOT NULL COMMENT '总成本',
  `description` varchar(255) DEFAULT NULL COMMENT '说明',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品配方表';

-- 插入配方数据
INSERT INTO `recipe` (`product_id`, `ingredient_name`, `quantity`, `unit`, `unit_cost`, `total_cost`, `description`, `sort`) VALUES
(1, '茶叶', 5.00, 'g', 0.50, 2.50, '优质红茶', 1),
(1, '牛奶', 200.00, 'ml', 0.01, 2.00, '新鲜牛奶', 2),
(1, '珍珠', 30.00, 'g', 0.08, 2.40, 'Q弹珍珠', 3),
(1, '糖浆', 20.00, 'ml', 0.05, 1.00, '蔗糖糖浆', 4),
(2, '茶叶', 5.00, 'g', 0.50, 2.50, '优质红茶', 1),
(2, '牛奶', 200.00, 'ml', 0.01, 2.00, '新鲜牛奶', 2),
(2, '波霸', 40.00, 'g', 0.10, 4.00, '大颗粒波霸', 3),
(2, '糖浆', 20.00, 'ml', 0.05, 1.00, '蔗糖糖浆', 4);
