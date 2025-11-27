-- ========================================
-- 奶茶小程序数据库设计
-- 数据库名：milk_tea
-- 作者：MilkTea Team
-- 创建时间�?025-11-13
-- ========================================

CREATE DATABASE IF NOT EXISTS `milk_tea` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `milk_tea`;

-- ========================================
-- 用户�?-- ========================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户�?,
  `password` varchar(255) NOT NULL COMMENT '密码（MD5加密�?,
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机�?,
  `openid` varchar(100) DEFAULT NULL COMMENT '微信openid',
  `member_level` int(11) DEFAULT '0' COMMENT '会员等级�?-普通会员，1-黄金会员�?-钻石会员',
  `points` int(11) DEFAULT '0' COMMENT '积分',
  `balance` decimal(10,2) DEFAULT '0.00' COMMENT '余额',
  `user_type` int(11) DEFAULT '0' COMMENT '用户类型�?-普通用户，1-管理�?,
  `status` int(11) DEFAULT '1' COMMENT '状态：0-禁用�?-正常',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识�?-未删除，1-已删�?,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时�?,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_phone` (`phone`),
  KEY `idx_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户�?;

-- ========================================
-- 商品分类�?-- ========================================
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父级分类ID�?表示一级分�?,
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `icon` varchar(255) DEFAULT NULL COMMENT '分类图标',
  `status` int(11) DEFAULT '1' COMMENT '状态：0-禁用�?-启用',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识�?-未删除，1-已删�?,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类�?;

-- ========================================
-- 商品�?-- ========================================
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
  `sales` int(11) DEFAULT '0' COMMENT '销�?,
  `nutrition` text COMMENT '营养成分',
  `status` int(11) DEFAULT '1' COMMENT '状态：0-下架�?-上架',
  `is_recommend` int(11) DEFAULT '0' COMMENT '是否推荐�?-否，1-�?,
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识�?-未删除，1-已删�?,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品�?;

-- ========================================
-- 订单�?-- ========================================
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单�?,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `username` varchar(50) DEFAULT NULL COMMENT '用户�?,
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金�?,
  `discount_amount` decimal(10,2) DEFAULT '0.00' COMMENT '优惠金额',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '实付金额',
  `actual_amount` decimal(10,2) NOT NULL COMMENT '实际支付金额',
  `pay_type` int(11) DEFAULT NULL COMMENT '支付方式�?-微信支付�?-余额支付�?-组合支付',
  `status` int(11) DEFAULT '0' COMMENT '订单状态：0-待支付，1-待制作，2-制作中，3-待取餐，4-已完成，5-已取消，6-退款中�?-已退�?,
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `refund_reason` varchar(255) DEFAULT NULL COMMENT '退款原�?,
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识�?-未删除，1-已删�?,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单�?;

-- ========================================
-- 订单明细�?-- ========================================
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单明细ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) NOT NULL COMMENT '商品名称',
  `product_image` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `price` decimal(10,2) NOT NULL COMMENT '单价',
  `quantity` int(11) NOT NULL COMMENT '数量',
  `sweetness` int(11) DEFAULT NULL COMMENT '甜度�?-无糖�?-三分�?-五分�?-七分�?-正常',
  `temperature` int(11) DEFAULT NULL COMMENT '温度�?-去冰�?-少冰�?-正常�?-�?,
  `toppings` varchar(255) DEFAULT NULL COMMENT '加料（JSON格式�?,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细�?;

-- ========================================
-- 用户地址�?-- ========================================
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `name` varchar(50) NOT NULL COMMENT '收货�?,
  `phone` varchar(20) NOT NULL COMMENT '联系电话',
  `province` varchar(50) DEFAULT NULL COMMENT '省份',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `district` varchar(50) DEFAULT NULL COMMENT '区县',
  `detail` varchar(255) NOT NULL COMMENT '详细地址',
  `tag` int(11) DEFAULT NULL COMMENT '地址标签�?-家庭�?-公司�?-学校�?-其他',
  `is_default` int(11) DEFAULT '0' COMMENT '是否默认�?-否，1-�?,
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识�?-未删除，1-已删�?,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址�?;

-- ========================================
-- 优惠券表
-- ========================================
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
  `name` varchar(100) NOT NULL COMMENT '优惠券名�?,
  `type` int(11) NOT NULL COMMENT '优惠券类型：1-满减券，2-折扣券，3-兑换�?,
  `discount` decimal(10,2) NOT NULL COMMENT '折扣金额或折扣率',
  `min_amount` decimal(10,2) DEFAULT '0.00' COMMENT '最低消费金�?,
  `total_count` int(11) NOT NULL COMMENT '发行数量',
  `received_count` int(11) DEFAULT '0' COMMENT '已领取数�?,
  `valid_start` datetime NOT NULL COMMENT '有效期开�?,
  `valid_end` datetime NOT NULL COMMENT '有效期结�?,
  `status` int(11) DEFAULT '1' COMMENT '状态：0-禁用�?-启用',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识�?-未删除，1-已删�?,
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
  `status` int(11) DEFAULT '0' COMMENT '状态：0-未使用，1-已使用，2-已过�?,
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
  `status` int(11) DEFAULT '1' COMMENT '状态：0-禁用�?-启用',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识�?-未删除，1-已删�?,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

-- ========================================
-- 评价�?-- ========================================
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `rating` int(11) NOT NULL COMMENT '评分�?-5�?,
  `content` text COMMENT '评价内容',
  `images` varchar(500) DEFAULT NULL COMMENT '图片（JSON格式�?,
  `is_anonymous` int(11) DEFAULT '0' COMMENT '是否匿名�?-否，1-�?,
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识�?-未删除，1-已删�?,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价�?;

-- ========================================
-- 退款申请表
-- ========================================
DROP TABLE IF EXISTS `refund_request`;
CREATE TABLE `refund_request` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '退款申请ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单�?,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `customer_name` varchar(50) DEFAULT NULL COMMENT '客户名称',
  `refund_amount` decimal(10,2) NOT NULL COMMENT '退款金�?,
  `reason` varchar(500) NOT NULL COMMENT '退款原�?,
  `status` int(11) DEFAULT '0' COMMENT '状态：0-待处理，1-已同意，2-已拒�?,
  `reject_reason` varchar(500) DEFAULT NULL COMMENT '拒绝原因',
  `process_time` datetime DEFAULT NULL COMMENT '处理时间',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识�?-未删除，1-已删�?,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款申请表';

-- ========================================
-- 投诉�?-- ========================================
DROP TABLE IF EXISTS `complaint`;
CREATE TABLE `complaint` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '投诉ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单�?,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `customer_name` varchar(50) DEFAULT NULL COMMENT '客户名称',
  `complaint_type` int(11) NOT NULL COMMENT '投诉类型�?-商品质量�?-服务态度�?-配送问题，4-其他',
  `content` varchar(1000) NOT NULL COMMENT '投诉内容',
  `images` varchar(500) DEFAULT NULL COMMENT '图片（JSON格式�?,
  `status` int(11) DEFAULT '0' COMMENT '状态：0-待处理，1-处理中，2-已解决，3-已关�?,
  `response` varchar(1000) DEFAULT NULL COMMENT '处理回复',
  `process_time` datetime DEFAULT NULL COMMENT '处理时间',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识�?-未删除，1-已删�?,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投诉�?;

-- ========================================
-- 初始化数�?-- ========================================

-- 插入管理员账户（用户名：admin，密码：admin123 - MD5加密�?INSERT INTO `user` (`username`, `password`, `nickname`, `user_type`, `status`) 
VALUES ('admin', '0192023a7bbd73250516f069df18b500', '系统管理�?, 1, 1);

-- 插入测试用户（用户名：user1，密码：123456 - MD5加密�?INSERT INTO `user` (`username`, `password`, `nickname`, `phone`, `member_level`, `points`, `balance`) 
VALUES ('user1', 'e10adc3949ba59abbe56e057f20f883e', '张三', '13800138000', 0, 100, 50.00);

-- 插入测试用户2（用户名：user2，密码：123456 - MD5加密�?INSERT INTO `user` (`username`, `password`, `nickname`, `phone`, `member_level`, `points`, `balance`) 
VALUES ('user2', 'e10adc3949ba59abbe56e057f20f883e', '李四', '13900139000', 1, 500, 100.00);

-- 插入商品分类
INSERT INTO `category` (`name`, `parent_id`, `sort`) VALUES
('茶饮', 0, 1),
('咖啡', 0, 2),
('小食', 0, 3),
('奶茶', 1, 1),
('果茶', 1, 2),
('奶盖�?, 1, 3);

-- 插入商品
INSERT INTO `product` (`name`, `category_id`, `description`, `image`, `price`, `member_price`, `cost`, `stock`, `sales`, `nutrition`, `status`, `is_recommend`, `sort`) VALUES
('珍珠奶茶', 1, '经典珍珠奶茶，香浓可�?, 'product1.jpg', 12.00, 10.80, 8.00, 100, 156, '热量�?00kcal', 1, 1, 1),
('波霸奶茶', 1, '大颗粒珍珠，Q弹有嚼劲', 'product2.jpg', 13.00, 11.70, 8.50, 100, 98, '热量�?10kcal', 1, 1, 2),
('红豆奶茶', 1, '香甜红豆配奶�?, 'product3.jpg', 14.00, 12.60, 9.00, 100, 78, '热量�?20kcal', 1, 0, 3),
('芋泥奶茶', 1, '浓郁芋泥，香甜可�?, 'product4.jpg', 15.00, 13.50, 10.00, 100, 145, '热量�?30kcal', 1, 1, 4),
('百香果茶', 2, '清新百香果，酸甜可口', 'product5.jpg', 16.00, 14.40, 10.50, 100, 112, '热量�?50kcal', 1, 1, 5),
('柠檬�?, 2, '新鲜柠檬，清爽解�?, 'product6.jpg', 14.00, 12.60, 9.00, 100, 89, '热量�?20kcal', 1, 0, 6),
('芝士奶盖�?, 2, '浓郁芝士奶盖', 'product7.jpg', 18.00, 16.20, 12.00, 100, 134, '热量�?80kcal', 1, 1, 7),
('美式咖啡', 2, '经典美式咖啡', 'product8.jpg', 15.00, 13.50, 10.00, 100, 67, '热量�?kcal', 1, 0, 8),
('拿铁咖啡', 2, '香浓拿铁', 'product9.jpg', 18.00, 16.20, 12.00, 100, 92, '热量�?80kcal', 1, 0, 9),
('芝士蛋糕', 3, '香滑芝士蛋糕', 'product10.jpg', 25.00, 22.50, 15.00, 50, 56, '热量�?50kcal', 1, 1, 10);

-- 插入优惠�?INSERT INTO `coupon` (`name`, `type`, `discount`, `min_amount`, `total_count`, `valid_start`, `valid_end`, `status`) VALUES
('新用户专享券', 1, 5.00, 20.00, 1000, '2024-01-01 00:00:00', '2025-12-31 23:59:59', 1),
('�?0�?0', 1, 10.00, 30.00, 500, '2024-01-01 00:00:00', '2025-12-31 23:59:59', 1),
('8折优惠券', 2, 0.80, 0.00, 300, '2024-01-01 00:00:00', '2025-12-31 23:59:59', 1);

-- 插入轮播�?INSERT INTO `banner` (`title`, `image`, `sort`, `status`) VALUES
('新品上市', 'https://via.placeholder.com/750x300?text=New+Products', 1, 1),
('限时特惠', 'https://via.placeholder.com/750x300?text=Special+Offer', 2, 1),
('会员专享', 'https://via.placeholder.com/750x300?text=Member+Exclusive', 3, 1);

-- 插入用户地址
INSERT INTO `user_address` (`user_id`, `name`, `phone`, `province`, `city`, `district`, `detail`, `tag`, `is_default`) VALUES
(2, '张三', '13800138000', '广东�?, '深圳�?, '南山�?, '科技园南�?, 1, 1),
(2, '张三', '13800138000', '广东�?, '深圳�?, '福田�?, '华强北商�?, 2, 0),
(3, '李四', '13900139000', '北京�?, '北京�?, '朝阳�?, '三里�?, 1, 1);

-- 插入用户优惠�?INSERT INTO `user_coupon` (`user_id`, `coupon_id`, `status`) VALUES
(2, 1, 0),
(2, 2, 0),
(3, 1, 0),
(3, 3, 1);

-- ========================================
-- 商品配方�?-- ========================================
DROP TABLE IF EXISTS `recipe`;
CREATE TABLE `recipe` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配方ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `ingredient_name` varchar(100) NOT NULL COMMENT '原料名称',
  `quantity` decimal(10,2) NOT NULL COMMENT '用量',
  `unit` varchar(20) NOT NULL COMMENT '单位',
  `unit_cost` decimal(10,2) NOT NULL COMMENT '单价',
  `total_cost` decimal(10,2) NOT NULL COMMENT '总成�?,
  `description` varchar(255) DEFAULT NULL COMMENT '说明',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识�?-未删除，1-已删�?,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品配方�?;

-- 插入配方数据
INSERT INTO `recipe` (`product_id`, `ingredient_name`, `quantity`, `unit`, `unit_cost`, `total_cost`, `description`, `sort`) VALUES
-- 珍珠奶茶 (ID: 1)
(1, '茶叶', 5.00, 'g', 0.50, 2.50, '优质红茶', 1),
(1, '牛奶', 200.00, 'ml', 0.01, 2.00, '新鲜牛奶', 2),
(1, '珍珠', 30.00, 'g', 0.08, 2.40, 'Q弹珍�?, 3),
(1, '糖浆', 20.00, 'ml', 0.05, 1.00, '蔗糖糖浆', 4),

-- 波霸奶茶 (ID: 2)
(2, '茶叶', 5.00, 'g', 0.50, 2.50, '优质红茶', 1),
(2, '牛奶', 200.00, 'ml', 0.01, 2.00, '新鲜牛奶', 2),
(2, '波霸', 40.00, 'g', 0.10, 4.00, '大颗粒波�?, 3),
(2, '糖浆', 20.00, 'ml', 0.05, 1.00, '蔗糖糖浆', 4),

-- 红豆奶茶 (ID: 3)
(3, '茶叶', 5.00, 'g', 0.50, 2.50, '优质红茶', 1),
(3, '牛奶', 200.00, 'ml', 0.01, 2.00, '新鲜牛奶', 2),
(3, '红豆', 50.00, 'g', 0.06, 3.00, '蜜制红豆', 3),
(3, '糖浆', 20.00, 'ml', 0.05, 1.00, '蔗糖糖浆', 4),

-- 芋泥奶茶 (ID: 4)
(4, '茶叶', 5.00, 'g', 0.50, 2.50, '优质红茶', 1),
(4, '牛奶', 200.00, 'ml', 0.01, 2.00, '新鲜牛奶', 2),
(4, '芋泥', 60.00, 'g', 0.12, 7.20, '香浓芋泥', 3),
(4, '糖浆', 15.00, 'ml', 0.05, 0.75, '蔗糖糖浆', 4),

-- 百香果茶 (ID: 5)
(5, '茶叶', 4.00, 'g', 0.50, 2.00, '优质绿茶', 1),
(5, '百香果浆', 80.00, 'ml', 0.15, 12.00, '新鲜百香果浆', 2),
(5, '蜂蜜', 25.00, 'ml', 0.08, 2.00, '天然蜂蜜', 3),
(5, '柠檬�?, 10.00, 'ml', 0.10, 1.00, '新鲜柠檬�?, 4),

-- 柠檬�?(ID: 6)
(6, '茶叶', 4.00, 'g', 0.50, 2.00, '优质绿茶', 1),
(6, '柠檬�?, 3.00, '�?, 0.50, 1.50, '新鲜柠檬�?, 2),
(6, '蜂蜜', 20.00, 'ml', 0.08, 1.60, '天然蜂蜜', 3),
(6, '薄荷�?, 5.00, '�?, 0.20, 1.00, '新鲜薄荷�?, 4),

-- 芝士奶盖�?(ID: 7)
(7, '茶叶', 5.00, 'g', 0.50, 2.50, '优质乌龙�?, 1),
(7, '芝士�?, 30.00, 'g', 0.20, 6.00, '进口芝士�?, 2),
(7, '淡奶�?, 100.00, 'ml', 0.03, 3.00, '动物性淡奶油', 3),
(7, '糖浆', 15.00, 'ml', 0.05, 0.75, '蔗糖糖浆', 4),

-- 美式咖啡 (ID: 8)
(8, '咖啡�?, 20.00, 'g', 0.80, 16.00, '优质阿拉比卡咖啡�?, 1),
(8, '热水', 300.00, 'ml', 0.001, 0.30, '纯净�?, 2),

-- 拿铁咖啡 (ID: 9)
(9, '咖啡�?, 18.00, 'g', 0.80, 14.40, '优质阿拉比卡咖啡�?, 1),
(9, '牛奶', 250.00, 'ml', 0.01, 2.50, '新鲜全脂牛奶', 2),
(9, '糖浆', 15.00, 'ml', 0.05, 0.75, '香草糖浆', 3),
(9, '奶泡', 50.00, 'ml', 0.02, 1.00, '细腻奶泡', 4),

-- 芝士蛋糕 (ID: 10)
(10, '奶油芝士', 100.00, 'g', 0.08, 8.00, '进口奶油芝士', 1),
(10, '消化饼干', 50.00, 'g', 0.02, 1.00, '消化饼干�?, 2),
(10, '鸡蛋', 1.00, '�?, 1.50, 1.50, '新鲜鸡蛋', 3),
(10, '细砂�?, 30.00, 'g', 0.01, 0.30, '细砂�?, 4),
(10, '香草�?, 2.00, 'ml', 0.50, 1.00, '天然香草�?, 5);

-- ========================================
-- 插入订单测试数据
-- ========================================

-- 插入测试订单
INSERT INTO `orders` (`order_no`, `user_id`, `username`, `total_amount`, `discount_amount`, `pay_amount`, `actual_amount`, `pay_type`, `status`, `remark`, `pay_time`, `create_time`) VALUES
('ORD202411190001', 2, '张三', 25.00, 3.00, 22.00, 22.00, 1, 4, '少糖少冰', '2024-11-19 09:30:00', '2024-11-19 09:25:00'),
('ORD202411190002', 3, '李四', 18.00, 0.00, 18.00, 18.00, 2, 3, '正常甜度', '2024-11-19 10:15:00', '2024-11-19 10:10:00'),
('ORD202411190003', 2, '张三', 31.00, 5.00, 26.00, 26.00, 1, 2, '热饮', '2024-11-19 11:20:00', '2024-11-19 11:15:00'),
('ORD202411190004', 3, '李四', 12.00, 0.00, 12.00, 12.00, 1, 1, '', '2024-11-19 12:05:00', '2024-11-19 12:00:00'),
('ORD202411190005', 2, '张三', 42.00, 8.00, 34.00, 34.00, 3, 4, '加珍�?, '2024-11-19 13:45:00', '2024-11-19 13:40:00'),
('ORD202411190006', 3, '李四', 15.00, 0.00, 15.00, 15.00, 1, 0, '微信支付', NULL, '2024-11-19 14:30:00'),
('ORD202411190007', 2, '张三', 28.00, 3.00, 25.00, 25.00, 1, 5, '用户取消', NULL, '2024-11-19 15:10:00'),
('ORD202411190008', 3, '李四', 36.00, 6.00, 30.00, 30.00, 2, 4, '会员折扣', '2024-11-19 16:20:00', '2024-11-19 16:15:00');

-- 插入订单明细
INSERT INTO `order_item` (`order_id`, `product_id`, `product_name`, `product_image`, `price`, `quantity`, `sweetness`, `temperature`, `toppings`) VALUES
-- 订单1的商�?(1, 1, '珍珠奶茶', '/api/uploads/images/sample/product1.jpg', 12.00, 1, 2, 1, '["珍珠"]'),
(1, 9, '拿铁咖啡', '/api/uploads/images/sample/product9.jpg', 18.00, 1, 3, 3, '[]'),

-- 订单2的商�?(2, 9, '拿铁咖啡', '/api/uploads/images/sample/product9.jpg', 18.00, 1, 3, 3, '[]'),

-- 订单3的商�?(3, 2, '波霸奶茶', '/api/uploads/images/sample/product2.jpg', 13.00, 1, 2, 2, '["波霸"]'),
(3, 9, '拿铁咖啡', '/api/uploads/images/sample/product9.jpg', 18.00, 1, 4, 3, '[]'),

-- 订单4的商�?(4, 1, '珍珠奶茶', '/api/uploads/images/sample/product1.jpg', 12.00, 1, 1, 1, '["珍珠"]'),

-- 订单5的商�?(5, 3, '红豆奶茶', '/api/uploads/images/sample/product3.jpg', 14.00, 1, 2, 2, '["红豆"]'),
(5, 7, '芝士奶盖�?, '/api/uploads/images/sample/product7.jpg', 18.00, 1, 3, 1, '["芝士奶盖"]'),
(5, 10, '芝士蛋糕', '/api/uploads/images/sample/product10.jpg', 25.00, 1, 0, 0, '[]'),

-- 订单6的商�?(6, 8, '美式咖啡', '/api/uploads/images/sample/product8.jpg', 15.00, 1, 0, 3, '[]'),

-- 订单7的商�?(7, 4, '芋泥奶茶', '/api/uploads/images/sample/product4.jpg', 15.00, 1, 2, 2, '["芋泥"]'),
(7, 6, '柠檬�?, '/api/uploads/images/sample/product6.jpg', 14.00, 1, 1, 1, '["柠檬�?, "薄荷�?]'),

-- 订单8的商�?(8, 5, '百香果茶', '/api/uploads/images/sample/product5.jpg', 16.00, 1, 2, 1, '["百香果浆"]'),
(8, 7, '芝士奶盖�?, '/api/uploads/images/sample/product7.jpg', 18.00, 1, 3, 2, '["芝士奶盖"]');





-- 更新商品图片路径
UPDATE `product` SET `image` = '/api/uploads/images/sample/product1.jpg' WHERE `id` = 1;
UPDATE `product` SET `image` = '/api/uploads/images/sample/product2.jpg' WHERE `id` = 2;
UPDATE `product` SET `image` = '/api/uploads/images/sample/product3.jpg' WHERE `id` = 3;
UPDATE `product` SET `image` = '/api/uploads/images/sample/product4.jpg' WHERE `id` = 4;
UPDATE `product` SET `image` = '/api/uploads/images/sample/product5.jpg' WHERE `id` = 5;
UPDATE `product` SET `image` = '/api/uploads/images/sample/product6.jpg' WHERE `id` = 6;
UPDATE `product` SET `image` = '/api/uploads/images/sample/product7.jpg' WHERE `id` = 7;
UPDATE `product` SET `image` = '/api/uploads/images/sample/product8.jpg' WHERE `id` = 8;
UPDATE `product` SET `image` = '/api/uploads/images/sample/product9.jpg' WHERE `id` = 9;
UPDATE `product` SET `image` = '/api/uploads/images/sample/product10.jpg' WHERE `id` = 10;

-- ========================================
-- 插入退款申请测试数�?-- ========================================
INSERT INTO `refund_request` (`order_id`, `order_no`, `user_id`, `customer_name`, `refund_amount`, `reason`, `status`, `reject_reason`, `process_time`, `create_time`) VALUES
(1, 'ORD202411190001', 2, '张三', 22.00, '商品质量问题，奶茶味道不�?, 0, NULL, NULL, '2024-11-19 10:30:00'),
(3, 'ORD202411190003', 2, '张三', 26.00, '等待时间太长，不想要�?, 0, NULL, NULL, '2024-11-19 12:00:00'),
(5, 'ORD202411190005', 2, '张三', 34.00, '配料不新�?, 1, NULL, '2024-11-19 14:30:00', '2024-11-19 14:00:00'),
(8, 'ORD202411190008', 3, '李四', 30.00, '口味不符合预�?, 2, '订单已完成且超过退款时�?, '2024-11-19 17:00:00', '2024-11-19 16:45:00'),
(2, 'ORD202411190002', 3, '李四', 18.00, '下错单了', 0, NULL, NULL, '2024-11-19 10:45:00');

-- ========================================
-- 插入投诉测试数据
-- ========================================
INSERT INTO `complaint` (`order_id`, `order_no`, `user_id`, `customer_name`, `complaint_type`, `content`, `images`, `status`, `response`, `process_time`, `create_time`) VALUES
(1, 'ORD202411190001', 2, '张三', 1, '奶茶里面有异物，怀疑是制作过程中混入的杂质，希望能够重视食品安全问�?, NULL, 0, NULL, NULL, '2024-11-19 10:00:00'),
(2, 'ORD202411190002', 3, '李四', 2, '店员态度很差，说话不礼貌，服务态度需要改�?, NULL, 1, '非常抱歉给您带来不好的体验，我们已经对相关员工进行了培训', '2024-11-19 11:00:00', '2024-11-19 10:30:00'),
(3, 'ORD202411190003', 2, '张三', 3, '配送时间太长，等了40分钟才送到，奶茶都凉了', NULL, 2, '非常抱歉，由于当时订单量较大导致配送延迟，我们已经优化了配送流程，并为您补偿了一张优惠券', '2024-11-19 12:30:00', '2024-11-19 12:00:00'),
(4, 'ORD202411190004', 3, '李四', 1, '珍珠煮的太硬了，口感很差', NULL, 0, NULL, NULL, '2024-11-19 12:30:00'),
(7, 'ORD202411190007', 2, '张三', 4, '包装破损，奶茶洒了一�?, NULL, 3, '已为您重新制作并配送，同时赠送了小食作为补偿', '2024-11-19 15:45:00', '2024-11-19 15:30:00'),
(8, 'ORD202411190008', 3, '李四', 1, '芝士奶盖不新鲜，有异�?, NULL, 0, NULL, NULL, '2024-11-19 16:50:00');




-- ========================================
-- 用户管理模块测试数据更新脚本
-- ========================================


-- ========================================
-- 添加更多测试用户数据
-- ========================================

-- 插入普通会�?INSERT INTO `user` (`username`, `password`, `nickname`, `phone`, `member_level`, `points`, `balance`, `user_type`, `status`, `create_time`, `last_login_time`) VALUES
('user3', 'e10adc3949ba59abbe56e057f20f883e', '王五', '13700137000', 0, 50, 20.00, 0, 1, '2024-11-01 10:00:00', '2024-11-18 15:30:00'),
('user4', 'e10adc3949ba59abbe56e057f20f883e', '赵六', '13600136000', 0, 80, 35.50, 0, 1, '2024-11-05 14:20:00', '2024-11-19 09:15:00'),
('user5', 'e10adc3949ba59abbe56e057f20f883e', '孙七', '13500135000', 0, 120, 50.00, 0, 1, '2024-11-08 16:45:00', '2024-11-19 11:20:00'),
('user6', 'e10adc3949ba59abbe56e057f20f883e', '周八', '13400134000', 0, 30, 15.00, 0, 1, '2024-11-10 09:30:00', '2024-11-17 14:00:00'),
('user7', 'e10adc3949ba59abbe56e057f20f883e', '吴九', '13300133000', 0, 60, 25.00, 0, 1, '2024-11-12 11:15:00', '2024-11-19 10:45:00');

-- 插入黄金会员
INSERT INTO `user` (`username`, `password`, `nickname`, `phone`, `member_level`, `points`, `balance`, `user_type`, `status`, `create_time`, `last_login_time`) VALUES
('vip1', 'e10adc3949ba59abbe56e057f20f883e', 'VIP张三', '13200132000', 1, 800, 200.00, 0, 1, '2024-10-15 10:00:00', '2024-11-19 08:30:00'),
('vip2', 'e10adc3949ba59abbe56e057f20f883e', 'VIP李四', '13100131000', 1, 650, 150.00, 0, 1, '2024-10-20 14:30:00', '2024-11-19 12:00:00'),
('vip3', 'e10adc3949ba59abbe56e057f20f883e', 'VIP王五', '13000130000', 1, 720, 180.00, 0, 1, '2024-10-25 16:00:00', '2024-11-18 16:30:00'),
('vip4', 'e10adc3949ba59abbe56e057f20f883e', 'VIP赵六', '12900129000', 1, 580, 120.00, 0, 1, '2024-11-01 09:00:00', '2024-11-19 14:15:00');

-- 插入钻石会员
INSERT INTO `user` (`username`, `password`, `nickname`, `phone`, `member_level`, `points`, `balance`, `user_type`, `status`, `create_time`, `last_login_time`) VALUES
('svip1', 'e10adc3949ba59abbe56e057f20f883e', 'SVIP张�?, '12800128000', 2, 2500, 500.00, 0, 1, '2024-09-01 10:00:00', '2024-11-19 09:00:00'),
('svip2', 'e10adc3949ba59abbe56e057f20f883e', 'SVIP李�?, '12700127000', 2, 2200, 450.00, 0, 1, '2024-09-15 14:00:00', '2024-11-19 11:30:00'),
('svip3', 'e10adc3949ba59abbe56e057f20f883e', 'SVIP王�?, '12600126000', 2, 1800, 380.00, 0, 1, '2024-10-01 16:00:00', '2024-11-18 15:00:00');

-- 插入今日新增用户
INSERT INTO `user` (`username`, `password`, `nickname`, `phone`, `member_level`, `points`, `balance`, `user_type`, `status`, `create_time`) VALUES
('newuser1', 'e10adc3949ba59abbe56e057f20f883e', '新用�?', '12500125000', 0, 0, 0.00, 0, 1, NOW()),
('newuser2', 'e10adc3949ba59abbe56e057f20f883e', '新用�?', '12400124000', 0, 0, 0.00, 0, 1, NOW()),
('newuser3', 'e10adc3949ba59abbe56e057f20f883e', '新用�?', '12300123000', 0, 0, 0.00, 0, 1, NOW());

-- 插入禁用用户（用于测试状态管理）
INSERT INTO `user` (`username`, `password`, `nickname`, `phone`, `member_level`, `points`, `balance`, `user_type`, `status`, `create_time`, `last_login_time`) VALUES
('disabled1', 'e10adc3949ba59abbe56e057f20f883e', '禁用用户1', '12200122000', 0, 20, 10.00, 0, 0, '2024-11-01 10:00:00', '2024-11-10 15:00:00'),
('disabled2', 'e10adc3949ba59abbe56e057f20f883e', '禁用用户2', '12100121000', 0, 15, 5.00, 0, 0, '2024-11-05 14:00:00', '2024-11-12 10:00:00');

-- ========================================
-- 为测试用户添加订单数据（用于统计�?-- ========================================

-- VIP用户的订�?INSERT INTO `orders` (`order_no`, `user_id`, `username`, `total_amount`, `discount_amount`, `pay_amount`, `actual_amount`, `pay_type`, `status`, `pay_time`, `finish_time`, `create_time`) VALUES
-- vip1的订�?('ORD202411150001', 7, 'VIP张三', 45.00, 5.00, 40.00, 40.00, 1, 4, '2024-11-15 10:30:00', '2024-11-15 11:00:00', '2024-11-15 10:25:00'),
('ORD202411160001', 7, 'VIP张三', 38.00, 4.00, 34.00, 34.00, 1, 4, '2024-11-16 14:20:00', '2024-11-16 14:50:00', '2024-11-16 14:15:00'),
('ORD202411170002', 7, 'VIP张三', 52.00, 6.00, 46.00, 46.00, 2, 4, '2024-11-17 16:10:00', '2024-11-17 16:40:00', '2024-11-17 16:05:00'),
('ORD202411180003', 7, 'VIP张三', 42.00, 5.00, 37.00, 37.00, 1, 4, '2024-11-18 11:30:00', '2024-11-18 12:00:00', '2024-11-18 11:25:00'),

-- vip2的订�?('ORD202411140001', 8, 'VIP李四', 35.00, 3.00, 32.00, 32.00, 1, 4, '2024-11-14 15:00:00', '2024-11-14 15:30:00', '2024-11-14 14:55:00'),
('ORD202411160002', 8, 'VIP李四', 48.00, 5.00, 43.00, 43.00, 1, 4, '2024-11-16 10:20:00', '2024-11-16 10:50:00', '2024-11-16 10:15:00'),
('ORD202411180004', 8, 'VIP李四', 40.00, 4.00, 36.00, 36.00, 2, 4, '2024-11-18 16:40:00', '2024-11-18 17:10:00', '2024-11-18 16:35:00'),

-- svip1的订单（钻石会员，消费更多）
('ORD202411130001', 11, 'SVIP张�?, 88.00, 10.00, 78.00, 78.00, 1, 4, '2024-11-13 12:00:00', '2024-11-13 12:30:00', '2024-11-13 11:55:00'),
('ORD202411140002', 11, 'SVIP张�?, 95.00, 12.00, 83.00, 83.00, 1, 4, '2024-11-14 14:30:00', '2024-11-14 15:00:00', '2024-11-14 14:25:00'),
('ORD202411150002', 11, 'SVIP张�?, 102.00, 15.00, 87.00, 87.00, 2, 4, '2024-11-15 16:00:00', '2024-11-15 16:30:00', '2024-11-15 15:55:00'),
('ORD202411160003', 11, 'SVIP张�?, 78.00, 8.00, 70.00, 70.00, 1, 4, '2024-11-16 11:20:00', '2024-11-16 11:50:00', '2024-11-16 11:15:00'),
('ORD202411170003', 11, 'SVIP张�?, 92.00, 10.00, 82.00, 82.00, 1, 4, '2024-11-17 13:40:00', '2024-11-17 14:10:00', '2024-11-17 13:35:00'),
('ORD202411180005', 11, 'SVIP张�?, 85.00, 9.00, 76.00, 76.00, 2, 4, '2024-11-18 15:20:00', '2024-11-18 15:50:00', '2024-11-18 15:15:00'),

-- 普通用户的订单
('ORD202411170004', 3, '王五', 28.00, 2.00, 26.00, 26.00, 1, 4, '2024-11-17 10:30:00', '2024-11-17 11:00:00', '2024-11-17 10:25:00'),
('ORD202411180006', 4, '赵六', 32.00, 0.00, 32.00, 32.00, 1, 4, '2024-11-18 14:20:00', '2024-11-18 14:50:00', '2024-11-18 14:15:00'),
('ORD202411190009', 5, '孙七', 25.00, 0.00, 25.00, 25.00, 1, 4, '2024-11-19 09:30:00', '2024-11-19 10:00:00', '2024-11-19 09:25:00');

-- ========================================
-- 为订单添加订单明�?-- ========================================

-- vip1的订单明�?INSERT INTO `order_item` (`order_id`, `product_id`, `product_name`, `product_image`, `price`, `quantity`, `sweetness`, `temperature`, `toppings`) VALUES
(9, 1, '珍珠奶茶', '/api/uploads/images/sample/product1.jpg', 12.00, 2, 2, 1, '["珍珠"]'),
(9, 4, '芋泥奶茶', '/api/uploads/images/sample/product4.jpg', 15.00, 1, 3, 2, '["芋泥"]'),
(10, 2, '波霸奶茶', '/api/uploads/images/sample/product2.jpg', 13.00, 2, 2, 1, '["波霸"]'),
(10, 1, '珍珠奶茶', '/api/uploads/images/sample/product1.jpg', 12.00, 1, 1, 1, '["珍珠"]'),
(11, 7, '芝士奶盖�?, '/api/uploads/images/sample/product7.jpg', 18.00, 2, 3, 1, '["芝士奶盖"]'),
(11, 5, '百香果茶', '/api/uploads/images/sample/product5.jpg', 16.00, 1, 2, 1, '["百香果浆"]'),
(12, 3, '红豆奶茶', '/api/uploads/images/sample/product3.jpg', 14.00, 2, 2, 2, '["红豆"]'),
(12, 1, '珍珠奶茶', '/api/uploads/images/sample/product1.jpg', 12.00, 1, 1, 1, '["珍珠"]');

-- svip1的订单明�?INSERT INTO `order_item` (`order_id`, `product_id`, `product_name`, `product_image`, `price`, `quantity`, `sweetness`, `temperature`, `toppings`) VALUES
(13, 7, '芝士奶盖�?, '/api/uploads/images/sample/product7.jpg', 18.00, 3, 3, 1, '["芝士奶盖"]'),
(13, 10, '芝士蛋糕', '/api/uploads/images/sample/product10.jpg', 25.00, 1, 0, 0, '[]'),
(14, 4, '芋泥奶茶', '/api/uploads/images/sample/product4.jpg', 15.00, 4, 3, 2, '["芋泥"]'),
(14, 5, '百香果茶', '/api/uploads/images/sample/product5.jpg', 16.00, 2, 2, 1, '["百香果浆"]'),
(15, 9, '拿铁咖啡', '/api/uploads/images/sample/product9.jpg', 18.00, 4, 4, 3, '[]'),
(15, 10, '芝士蛋糕', '/api/uploads/images/sample/product10.jpg', 25.00, 1, 0, 0, '[]');

SELECT '用户管理模块测试数据更新完成�? AS message;
SELECT CONCAT('总用户数: ', COUNT(*)) AS user_count FROM user;
SELECT CONCAT('普通会�? ', COUNT(*)) AS normal_count FROM user WHERE member_level = 0;
SELECT CONCAT('黄金会员: ', COUNT(*)) AS gold_count FROM user WHERE member_level = 1;
SELECT CONCAT('钻石会员: ', COUNT(*)) AS diamond_count FROM user WHERE member_level = 2;
SELECT CONCAT('今日新增: ', COUNT(*)) AS today_count FROM user WHERE DATE(create_time) = CURDATE();



-- ========================================
-- 营销活动模块数据库设�?-- ========================================


-- ========================================
-- 营销活动�?-- ========================================
DROP TABLE IF EXISTS `marketing_activity`;
CREATE TABLE `marketing_activity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `name` varchar(100) NOT NULL COMMENT '活动名称',
  `type` varchar(20) NOT NULL COMMENT '活动类型：discount-满减，seckill-秒杀，gift-买赠，member-会员专享',
  `description` varchar(500) DEFAULT NULL COMMENT '活动描述',
  `rules` text COMMENT '活动规则',
  `banner` varchar(255) DEFAULT NULL COMMENT '活动横幅图片',
  `start_time` datetime NOT NULL COMMENT '开始时�?,
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `participants` int(11) DEFAULT '0' COMMENT '参与人数',
  `revenue` decimal(10,2) DEFAULT '0.00' COMMENT '活动收益',
  `status` int(11) DEFAULT '0' COMMENT '状态：0-未开始，1-进行中，2-已结束，3-已暂�?,
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识�?-未删除，1-已删�?,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_end_time` (`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营销活动�?;

-- 插入测试数据
INSERT INTO `marketing_activity` (`name`, `type`, `description`, `rules`, `banner`, `start_time`, `end_time`, `participants`, `revenue`, `status`) VALUES
('�?1狂欢�?, 'discount', '全场�?0�?0，满100�?5', '活动期间，单笔订单满50元减10元，�?00元减25�?, '/images/activity1.jpg', '2024-11-11 00:00:00', '2024-11-11 23:59:59', 580, 15600.00, 2),
('新品秒杀', 'seckill', '新品限时秒杀，每�?0点开�?, '每日10:00-12:00，指定新�?折秒杀，限�?00�?, '/images/activity2.jpg', '2024-11-15 00:00:00', '2024-11-30 23:59:59', 320, 8900.00, 1),
('买一送一', 'gift', '指定商品买一送一', '购买指定商品，赠送同款商品一�?, '/images/activity3.jpg', '2024-11-20 00:00:00', '2024-11-25 23:59:59', 180, 5200.00, 1),
('会员�?, 'member', '会员专享8折优�?, '每周三会员日，全场商品会�?�?, '/images/activity4.jpg', '2024-11-01 00:00:00', '2024-12-31 23:59:59', 450, 12800.00, 1),
('圣诞特惠', 'discount', '圣诞节满减活�?, '圣诞节期间，�?8�?8，满128�?8', '/images/activity5.jpg', '2024-12-24 00:00:00', '2024-12-26 23:59:59', 0, 0.00, 0);




-- ========================================
-- 打印管理模块数据库更新脚�?-- ========================================



-- ========================================
-- 打印设备�?-- ========================================
DROP TABLE IF EXISTS `print_device`;
CREATE TABLE `print_device` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '设备ID',
  `name` varchar(100) NOT NULL COMMENT '设备名称',
  `type` varchar(20) NOT NULL COMMENT '设备类型：thermal-热敏，dot-针式，laser-激�?,
  `connection` varchar(20) NOT NULL COMMENT '连接方式：network-网络，usb-USB，bluetooth-蓝牙',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `port` int(11) DEFAULT NULL COMMENT '端口',
  `path` varchar(255) DEFAULT NULL COMMENT 'USB设备路径',
  `mac` varchar(50) DEFAULT NULL COMMENT '蓝牙MAC地址',
  `status` int(11) DEFAULT '1' COMMENT '状态：0-离线�?-在线',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识�?-未删除，1-已删�?,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打印设备�?;

-- ========================================
-- 打印模板�?-- ========================================
DROP TABLE IF EXISTS `print_template`;
CREATE TABLE `print_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `name` varchar(100) NOT NULL COMMENT '模板名称',
  `type` varchar(20) NOT NULL COMMENT '模板类型：order-订单小票，kitchen-厨房单，delivery-配送单',
  `width` int(11) NOT NULL COMMENT '纸张宽度(mm)',
  `content` text NOT NULL COMMENT '模板内容',
  `header` varchar(500) DEFAULT NULL COMMENT '页眉',
  `footer` varchar(500) DEFAULT NULL COMMENT '页脚',
  `is_default` int(11) DEFAULT '0' COMMENT '是否默认�?-否，1-�?,
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识�?-未删除，1-已删�?,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打印模板�?;

-- ========================================
-- 打印记录表（优化�?- 减少冗余�?-- ========================================
DROP TABLE IF EXISTS `print_record`;
CREATE TABLE `print_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `device_id` bigint(20) NOT NULL COMMENT '设备ID',
  `template_id` bigint(20) NOT NULL COMMENT '模板ID',
  `status` int(11) DEFAULT '1' COMMENT '状态：1-成功�?-失败�?-处理�?,
  `error_message` varchar(500) DEFAULT NULL COMMENT '错误信息',
  `copies` int(11) DEFAULT '1' COMMENT '打印份数',
  `duration` int(11) DEFAULT NULL COMMENT '打印耗时(�?',
  `retry_count` int(11) DEFAULT '0' COMMENT '重试次数',
  `print_time` datetime DEFAULT NULL COMMENT '打印时间',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识�?-未删除，1-已删�?,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_device_id` (`device_id`),
  KEY `idx_template_id` (`template_id`),
  CONSTRAINT `fk_print_record_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `fk_print_record_device` FOREIGN KEY (`device_id`) REFERENCES `print_device` (`id`),
  CONSTRAINT `fk_print_record_template` FOREIGN KEY (`template_id`) REFERENCES `print_template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打印记录�?;

-- ========================================
-- 插入测试数据
-- ========================================

-- 插入打印设备
INSERT INTO `print_device` (`name`, `type`, `connection`, `ip`, `port`, `status`, `description`) VALUES
('前台打印�?, 'thermal', 'network', '192.168.1.100', 9100, 1, '前台收银打印机，用于打印订单小票'),
('厨房打印�?, 'thermal', 'network', '192.168.1.101', 9100, 1, '厨房订单打印机，用于打印制作�?),
('备用打印�?, 'thermal', 'network', '192.168.1.102', 9100, 0, '备用打印机，离线状�?);

-- 插入打印模板
INSERT INTO `print_template` (`name`, `type`, `width`, `content`, `header`, `footer`, `is_default`) VALUES
('标准订单小票', 'order', 80, 
'订单�? {orderNo}
客户: {customerName}
--------------------------------
商品清单:
{items}
--------------------------------
订单金额: {totalAmount}
下单时间: {createTime}',
'========== 奶茶小店 ==========',
'谢谢惠顾，欢迎再次光临！
客服电话: 400-123-4567
==============================',
1),

('厨房制作�?, 'kitchen', 80,
'【制作单�?订单�? {orderNo}
--------------------------------
制作清单:
{items}
--------------------------------
下单时间: {createTime}
备注: 请按顺序制作',
'========== 厨房制作�?==========',
'==============================',
1),

('配送单', 'delivery', 80,
'【配送单�?订单�? {orderNo}
客户: {customerName}
--------------------------------
配送商�?
{items}
--------------------------------
配送地址: 待补�?联系电话: 待补�?下单时间: {createTime}',
'========== 配送单 ==========',
'请核对商品后配�?==============================',
1);

-- 插入打印记录（优化版 - 只存储关键信息）
INSERT INTO `print_record` (`order_id`, `device_id`, `template_id`, `status`, `copies`, `duration`, `retry_count`, `print_time`, `error_message`) VALUES
(1, 1, 1, 1, 1, 2, 0, '2024-11-19 09:26:00', NULL),
(2, 2, 2, 1, 1, 2, 0, '2024-11-19 10:11:00', NULL),
(3, 1, 1, 1, 1, 2, 0, '2024-11-19 11:16:00', NULL),
(4, 3, 1, 2, 1, 0, 1, '2024-11-19 12:01:00', '设备离线，无法连�?);

SELECT '打印管理模块数据库更新完成！' AS message;
SELECT CONCAT('打印设备数量: ', COUNT(*)) AS device_count FROM print_device;
SELECT CONCAT('打印模板数量: ', COUNT(*)) AS template_count FROM print_template;
SELECT CONCAT('打印记录数量: ', COUNT(*)) AS record_count FROM print_record;


-- ========================================
-- 员工管理模块数据库脚�?-- ========================================



-- ========================================
-- 员工�?-- ========================================
DROP TABLE IF EXISTS `staff`;
CREATE TABLE `staff` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '员工ID',
  `username` varchar(50) NOT NULL COMMENT '用户�?,
  `password` varchar(255) NOT NULL COMMENT '密码（MD5加密�?,
  `name` varchar(50) NOT NULL COMMENT '员工姓名',
  `phone` varchar(20) NOT NULL COMMENT '手机�?,
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `employee_id` varchar(50) NOT NULL COMMENT '工号',
  `role` varchar(50) NOT NULL DEFAULT 'cashier' COMMENT '角色：super_admin-超级管理员，manager-店长，cashier-收银员，maker-制作�?,
  `department` varchar(50) DEFAULT NULL COMMENT '部门：management-管理部，front-前台部，kitchen-制作部，delivery-配送部',
  `hire_date` date DEFAULT NULL COMMENT '入职时间',
  `salary` decimal(10,2) DEFAULT '0.00' COMMENT '薪资',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `permissions` text COMMENT '权限（JSON格式�?,
  `status` int(11) DEFAULT '1' COMMENT '状态：0-离职�?-在职',
  `last_login` datetime DEFAULT NULL COMMENT '最后登录时�?,
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识�?-未删除，1-已删�?,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_employee_id` (`employee_id`),
  KEY `idx_phone` (`phone`),
  KEY `idx_role` (`role`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工�?;

-- 插入测试数据
INSERT INTO `staff` (`username`, `password`, `name`, `phone`, `email`, `employee_id`, `role`, `department`, `hire_date`, `salary`, `address`, `permissions`, `status`, `last_login`) VALUES
('admin', MD5('123456'), '张三', '13800138001', 'zhangsan@milktea.com', 'EMP001', 'super_admin', 'management', '2023-01-15', 10000.00, '北京市朝阳区', '["dashboard.view","dashboard.export","product.view","product.create","product.edit","product.delete","order.view","order.process","order.refund","user.view","user.edit","system.staff","system.config"]', 1, NOW()),
('manager01', MD5('123456'), '李四', '13800138002', 'lisi@milktea.com', 'EMP002', 'manager', 'management', '2023-03-20', 8000.00, '北京市海淀�?, '["dashboard.view","product.view","product.edit","order.view","order.process","user.view"]', 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('cashier01', MD5('123456'), '王五', '13800138003', 'wangwu@milktea.com', 'EMP003', 'cashier', 'front', '2023-05-10', 5000.00, '北京市西城区', '["order.view","order.process","product.view"]', 1, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
('maker01', MD5('123456'), '赵六', '13800138004', 'zhaoliu@milktea.com', 'EMP004', 'maker', 'kitchen', '2023-06-15', 5500.00, '北京市东城区', '["order.view","product.view"]', 1, DATE_SUB(NOW(), INTERVAL 5 HOUR)),
('cashier02', MD5('123456'), '孙七', '13800138005', 'sunqi@milktea.com', 'EMP005', 'cashier', 'front', '2023-08-01', 4800.00, '北京市丰台区', '["order.view","order.process","product.view"]', 1, DATE_SUB(NOW(), INTERVAL 3 DAY)),
('maker02', MD5('123456'), '周八', '13800138006', 'zhouba@milktea.com', 'EMP006', 'maker', 'kitchen', '2023-09-20', 5200.00, '北京市石景山�?, '["order.view","product.view"]', 0, DATE_SUB(NOW(), INTERVAL 30 DAY));


-- ========================================
-- 门店管理模块数据库脚�?-- ========================================



-- ========================================
-- 门店�?-- ========================================
DROP TABLE IF EXISTS `store`;
CREATE TABLE `store` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '门店ID',
  `name` varchar(100) NOT NULL COMMENT '门店名称',
  `address` varchar(255) NOT NULL COMMENT '门店地址',
  `phone` varchar(20) NOT NULL COMMENT '联系电话',
  `manager` varchar(50) DEFAULT NULL COMMENT '店长姓名',
  `manager_phone` varchar(20) DEFAULT NULL COMMENT '店长电话',
  `business_hours` varchar(100) DEFAULT NULL COMMENT '营业时间（JSON格式，如：["09:00","22:00"]�?,
  `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度',
  `area` decimal(10,2) DEFAULT NULL COMMENT '门店面积（平方米�?,
  `staff_count` int(11) DEFAULT '0' COMMENT '员工数量',
  `description` text COMMENT '门店描述',
  `images` text COMMENT '门店图片（JSON格式�?,
  `status` int(11) DEFAULT '1' COMMENT '状态：0-关闭�?-营业',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识�?-未删除，1-已删�?,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门店�?;

-- 插入测试数据
INSERT INTO `store` (`name`, `address`, `phone`, `manager`, `manager_phone`, `business_hours`, `latitude`, `longitude`, `area`, `staff_count`, `description`, `status`) VALUES
('总店', '北京市朝阳区建国�?8�?, '010-12345678', '张三', '13800138001', '["09:00","22:00"]', 39.904989, 116.407526, 150.00, 15, '总店位于市中心，交通便利，环境优雅', 1),
('海淀分店', '北京市海淀区中关村大街1�?, '010-23456789', '李四', '13800138002', '["09:00","22:00"]', 39.983424, 116.318977, 120.00, 10, '位于中关村核心区域，科技氛围浓厚', 1),
('西城分店', '北京市西城区西单北大�?20�?, '010-34567890', '王五', '13800138003', '["10:00","21:00"]', 39.913418, 116.374328, 100.00, 8, '西单商圈黄金位置，客流量�?, 1),
('东城分店', '北京市东城区王府井大�?38�?, '010-45678901', '赵六', '13800138004', '["09:30","21:30"]', 39.909187, 116.416357, 130.00, 12, '王府井步行街旁，游客众多', 1),
('丰台分店', '北京市丰台区丰台�?00�?, '010-56789012', '孙七', '13800138005', '["09:00","22:00"]', 39.858427, 116.287123, 110.00, 9, '丰台区域中心店，服务周边社区', 0);


-- ========================================
-- 系统配置模块数据库脚�?-- ========================================



-- ========================================
-- 系统配置�?-- ========================================

DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(100) NOT NULL COMMENT '配置�?,
  `config_value` text COMMENT '配置�?,
  `description` varchar(255) DEFAULT NULL COMMENT '配置描述',
  `type` int(11) DEFAULT '1' COMMENT '配置类型�?-文本�?-数字�?-布尔�?-JSON',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置�?;

-- 插入默认配置数据
INSERT INTO `system_config` (`config_key`, `config_value`, `description`, `type`) VALUES
-- 基本信息
('shop_name', '奶茶小铺', '店铺名称', 1),
('shop_logo', '', '店铺Logo', 1),
('shop_phone', '400-123-4567', '联系电话', 1),
('shop_address', '北京市朝阳区建国�?8�?, '联系地址', 1),

-- 支付配置
('payment_wechat_enabled', 'true', '微信支付开�?, 3),
('payment_alipay_enabled', 'false', '支付宝支付开�?, 3),
('payment_balance_enabled', 'true', '余额支付开�?, 3),

-- 订单配置
('order_auto_cancel_time', '30', '订单自动取消时间（分钟）', 2),
('order_prepare_time', '15', '预计制作时间（分钟）', 2),
('order_auto_complete_time', '24', '订单自动完成时间（小时）', 2),

-- 积分规则
('points_earn_rate', '1.0', '消费获取积分比例（消�?元获得积分）', 2),
('points_redeem_rate', '1.0', '积分抵扣比例�?00积分可抵扣金额）', 2),
('points_min_redeem', '100', '最低抵扣积�?, 2),

-- 会员配置
('member_upgrade_gold', '1000', '升级黄金会员消费金额', 2),
('member_upgrade_diamond', '5000', '升级钻石会员消费金额', 2),
('member_discount_gold', '0.95', '黄金会员折扣', 2),
('member_discount_diamond', '0.90', '钻石会员折扣', 2),

-- 系统配置
('system_maintenance', 'false', '系统维护模式', 3),
('system_register_enabled', 'true', '允许用户注册', 3),
('system_backup_auto', 'true', '自动备份开�?, 3),
('system_backup_time', '03:00', '自动备份时间', 1),
('system_last_backup', '', '最后备份时�?, 1);
-- 新增接口所需的数据表（简化版�?
-- 执行时间: 2024-11-26
-- 使用说明：请先选择 milk_tea 数据库，然后执行此脚�?

USE `milk_tea`;

-- 1. 积分商品�?
CREATE TABLE IF NOT EXISTS `points_product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '商品名称',
  `description` varchar(500) DEFAULT NULL COMMENT '商品描述',
  `image` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `points` int NOT NULL COMMENT '所需积分',
  `stock` int NOT NULL DEFAULT '0' COMMENT '库存数量',
  `exchanged` int NOT NULL DEFAULT '0' COMMENT '已兑换数�?,
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-下架�?-上架',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识�?-未删除，1-已删�?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分商品�?;

-- 2. 积分历史�?
CREATE TABLE IF NOT EXISTS `points_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `type` varchar(20) NOT NULL COMMENT '积分变化类型：earn-获得，use-使用',
  `points` int NOT NULL COMMENT '积分数量（正数为获得，负数为使用�?,
  `reason` varchar(200) NOT NULL COMMENT '原因/描述',
  `order_id` bigint DEFAULT NULL COMMENT '关联订单ID',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识�?-未删除，1-已删�?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分历史�?;

-- 3. 商品收藏�?
CREATE TABLE IF NOT EXISTS `product_collection` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识�?-未删除，1-已删�?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品收藏�?;

-- 4. 插入测试数据 - 积分商品
INSERT INTO `points_product` (`name`, `description`, `image`, `points`, `stock`, `exchanged`, `status`, `sort`) VALUES
('珍珠奶茶�?, '可兑换任意一杯珍珠奶�?, '/static/points/product1.jpg', 100, 1000, 0, 1, 1),
('芝士奶盖�?, '可兑换任意一杯芝士奶�?, '/static/points/product2.jpg', 150, 800, 0, 1, 2),
('5元优惠券', '全场通用5元优惠券', '/static/points/product3.jpg', 50, 2000, 0, 1, 3),
('10元优惠券', '全场通用10元优惠券', '/static/points/product4.jpg', 100, 1500, 0, 1, 4),
('奶茶杯套', '精美奶茶杯套一�?, '/static/points/product5.jpg', 200, 500, 0, 1, 5),
('品牌帆布�?, '品牌定制帆布�?, '/static/points/product6.jpg', 300, 300, 0, 1, 6);

-- 5. 订单表字段检�?
-- 注意：原始数据库中订单表名为 `orders`，且 finish_time �?refund_reason 字段已存�?
-- 如果您的订单表名�?`order`，请取消下面的注释并执行

-- ALTER TABLE `order` ADD COLUMN `finish_time` datetime DEFAULT NULL COMMENT '完成时间' AFTER `cancel_time`;
-- ALTER TABLE `order` ADD COLUMN `refund_reason` varchar(500) DEFAULT NULL COMMENT '退款原�? AFTER `finish_time`;

-- 完成
SELECT '新增接口数据表创建完成！' AS message;
SELECT '注意：订单表字段 finish_time �?refund_reason 已在原始数据库中存在' AS notice;
-- 更新商品图片为国内可访问的图片地址
-- 使用 picsum.photos (国内可访问的随机图片服务)

USE milk_tea;

-- 更新商品表的图片地址
-- 使用 picsum.photos 提供的随机图�?
UPDATE product SET image = 'https://picsum.photos/400/400?random=1' WHERE id = 1;
UPDATE product SET image = 'https://picsum.photos/400/400?random=2' WHERE id = 2;
UPDATE product SET image = 'https://picsum.photos/400/400?random=3' WHERE id = 3;
UPDATE product SET image = 'https://picsum.photos/400/400?random=4' WHERE id = 4;
UPDATE product SET image = 'https://picsum.photos/400/400?random=5' WHERE id = 5;
UPDATE product SET image = 'https://picsum.photos/400/400?random=6' WHERE id = 6;
UPDATE product SET image = 'https://picsum.photos/400/400?random=7' WHERE id = 7;
UPDATE product SET image = 'https://picsum.photos/400/400?random=8' WHERE id = 8;
UPDATE product SET image = 'https://picsum.photos/400/400?random=9' WHERE id = 9;
UPDATE product SET image = 'https://picsum.photos/400/400?random=10' WHERE id = 10;

-- 更新轮播�?
UPDATE banner SET image = 'https://picsum.photos/750/300?random=11' WHERE id = 1;
UPDATE banner SET image = 'https://picsum.photos/750/300?random=12' WHERE id = 2;
UPDATE banner SET image = 'https://picsum.photos/750/300?random=13' WHERE id = 3;

-- 更新积分商品图片
UPDATE points_product SET image = 'https://picsum.photos/300/300?random=21' WHERE id = 1;
UPDATE points_product SET image = 'https://picsum.photos/300/300?random=22' WHERE id = 2;
UPDATE points_product SET image = 'https://picsum.photos/300/300?random=23' WHERE id = 3;
UPDATE points_product SET image = 'https://picsum.photos/300/300?random=24' WHERE id = 4;
UPDATE points_product SET image = 'https://picsum.photos/300/300?random=25' WHERE id = 5;
UPDATE points_product SET image = 'https://picsum.photos/300/300?random=26' WHERE id = 6;



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


-- ========================================
-- 添加更多商品数据
-- 为各个分类补充商品
-- ========================================

USE `milk_tea`;

-- ========================================
-- 奶茶类商品 (category_id = 4)
-- ========================================
INSERT INTO `product` (`name`, `category_id`, `description`, `image`, `price`, `member_price`, `cost`, `stock`, `sales`, `nutrition`, `status`, `is_recommend`, `sort`) VALUES
('布蕾奶茶', 4, '焦糖布蕾风味，香甜丝滑', 'https://picsum.photos/400/400?random=11', 16.00, 14.40, 10.50, 100, 88, '热量：420kcal', 1, 1, 11),
('仙草奶茶', 4, '清凉仙草，解暑佳品', 'https://picsum.photos/400/400?random=12', 13.00, 11.70, 8.50, 100, 95, '热量：380kcal', 1, 0, 12),
('双拼奶茶', 4, '珍珠+椰果双重口感', 'https://picsum.photos/400/400?random=13', 15.00, 13.50, 9.50, 100, 102, '热量：430kcal', 1, 1, 13),
('焦糖奶茶', 4, '浓郁焦糖香，甜而不腻', 'https://picsum.photos/400/400?random=14', 14.00, 12.60, 9.00, 100, 76, '热量：410kcal', 1, 0, 14),
('巧克力奶茶', 4, '丝滑巧克力，醇厚浓香', 'https://picsum.photos/400/400?random=15', 16.00, 14.40, 10.50, 100, 84, '热量：450kcal', 1, 1, 15),
('椰果奶茶', 4, 'Q弹椰果，清爽可口', 'https://picsum.photos/400/400?random=16', 13.00, 11.70, 8.50, 100, 91, '热量：390kcal', 1, 0, 16),
('抹茶奶茶', 4, '日式抹茶，清新淡雅', 'https://picsum.photos/400/400?random=17', 15.00, 13.50, 10.00, 100, 87, '热量：400kcal', 1, 1, 17),
('紫薯奶茶', 4, '香甜紫薯，营养健康', 'https://picsum.photos/400/400?random=18', 15.00, 13.50, 10.00, 100, 69, '热量：420kcal', 1, 0, 18);

-- ========================================
-- 果茶类商品 (category_id = 5)
-- ========================================
INSERT INTO `product` (`name`, `category_id`, `description`, `image`, `price`, `member_price`, `cost`, `stock`, `sales`, `nutrition`, `status`, `is_recommend`, `sort`) VALUES
('草莓果茶', 5, '新鲜草莓，酸甜可口', 'https://picsum.photos/400/400?random=21', 18.00, 16.20, 12.00, 100, 125, '热量：180kcal', 1, 1, 21),
('芒果果茶', 5, '香甜芒果，热带风情', 'https://picsum.photos/400/400?random=22', 18.00, 16.20, 12.00, 100, 118, '热量：190kcal', 1, 1, 22),
('西瓜果茶', 5, '清爽西瓜，夏日必备', 'https://picsum.photos/400/400?random=23', 16.00, 14.40, 10.50, 100, 96, '热量：160kcal', 1, 0, 23),
('蜜桃果茶', 5, '香甜蜜桃，果香浓郁', 'https://picsum.photos/400/400?random=24', 17.00, 15.30, 11.00, 100, 108, '热量：170kcal', 1, 1, 24),
('葡萄柚果茶', 5, '清新葡萄柚，酸甜平衡', 'https://picsum.photos/400/400?random=25', 17.00, 15.30, 11.00, 100, 89, '热量：150kcal', 1, 0, 25),
('荔枝果茶', 5, '香甜荔枝，清爽怡人', 'https://picsum.photos/400/400?random=26', 18.00, 16.20, 12.00, 100, 94, '热量：180kcal', 1, 1, 26),
('橙子果茶', 5, '新鲜橙子，维C满满', 'https://picsum.photos/400/400?random=27', 15.00, 13.50, 10.00, 100, 102, '热量：140kcal', 1, 0, 27),
('奇异果茶', 5, '酸甜奇异果，营养丰富', 'https://picsum.photos/400/400?random=28', 17.00, 15.30, 11.00, 100, 78, '热量：160kcal', 1, 0, 28);

-- ========================================
-- 奶盖茶类商品 (category_id = 6)
-- ========================================
INSERT INTO `product` (`name`, `category_id`, `description`, `image`, `price`, `member_price`, `cost`, `stock`, `sales`, `nutrition`, `status`, `is_recommend`, `sort`) VALUES
('草莓奶盖', 6, '草莓果茶配芝士奶盖', 'https://picsum.photos/400/400?random=31', 20.00, 18.00, 13.00, 100, 115, '热量：380kcal', 1, 1, 31),
('芒果奶盖', 6, '芒果果茶配芝士奶盖', 'https://picsum.photos/400/400?random=32', 20.00, 18.00, 13.00, 100, 108, '热量：390kcal', 1, 1, 32),
('乌龙奶盖', 6, '乌龙茶配芝士奶盖', 'https://picsum.photos/400/400?random=33', 19.00, 17.10, 12.50, 100, 97, '热量：360kcal', 1, 0, 33),
('茉莉奶盖', 6, '茉莉绿茶配芝士奶盖', 'https://picsum.photos/400/400?random=34', 19.00, 17.10, 12.50, 100, 89, '热量：350kcal', 1, 0, 34),
('红茶奶盖', 6, '红茶配芝士奶盖', 'https://picsum.photos/400/400?random=35', 18.00, 16.20, 12.00, 100, 112, '热量：340kcal', 1, 1, 35),
('抹茶奶盖', 6, '抹茶配芝士奶盖', 'https://picsum.photos/400/400?random=36', 20.00, 18.00, 13.00, 100, 95, '热量：370kcal', 1, 1, 36),
('蜜桃奶盖', 6, '蜜桃果茶配芝士奶盖', 'https://picsum.photos/400/400?random=37', 20.00, 18.00, 13.00, 100, 86, '热量：380kcal', 1, 0, 37);

-- ========================================
-- 咖啡类商品 (category_id = 2)
-- ========================================
INSERT INTO `product` (`name`, `category_id`, `description`, `image`, `price`, `member_price`, `cost`, `stock`, `sales`, `nutrition`, `status`, `is_recommend`, `sort`) VALUES
('卡布奇诺', 2, '经典意式咖啡', 'https://picsum.photos/400/400?random=41', 18.00, 16.20, 12.00, 100, 78, '热量：180kcal', 1, 0, 41),
('摩卡咖啡', 2, '巧克力风味咖啡', 'https://picsum.photos/400/400?random=42', 20.00, 18.00, 13.00, 100, 85, '热量：220kcal', 1, 1, 42),
('焦糖玛奇朵', 2, '焦糖风味拿铁', 'https://picsum.photos/400/400?random=43', 22.00, 19.80, 14.00, 100, 92, '热量：250kcal', 1, 1, 43),
('香草拿铁', 2, '香草风味拿铁', 'https://picsum.photos/400/400?random=44', 19.00, 17.10, 12.50, 100, 73, '热量：200kcal', 1, 0, 44),
('冰美式', 2, '冰镇美式咖啡', 'https://picsum.photos/400/400?random=45', 15.00, 13.50, 10.00, 100, 88, '热量：10kcal', 1, 0, 45),
('冰拿铁', 2, '冰镇拿铁咖啡', 'https://picsum.photos/400/400?random=46', 18.00, 16.20, 12.00, 100, 95, '热量：180kcal', 1, 1, 46);

-- ========================================
-- 小食类商品 (category_id = 3)
-- ========================================
INSERT INTO `product` (`name`, `category_id`, `description`, `image`, `price`, `member_price`, `cost`, `stock`, `sales`, `nutrition`, `status`, `is_recommend`, `sort`) VALUES
('提拉米苏', 3, '经典意式甜点', 'https://picsum.photos/400/400?random=51', 28.00, 25.20, 18.00, 50, 45, '热量：380kcal', 1, 1, 51),
('布朗尼', 3, '浓郁巧克力蛋糕', 'https://picsum.photos/400/400?random=52', 22.00, 19.80, 14.00, 50, 52, '热量：320kcal', 1, 0, 52),
('抹茶蛋糕', 3, '清新抹茶风味', 'https://picsum.photos/400/400?random=53', 25.00, 22.50, 16.00, 50, 38, '热量：300kcal', 1, 1, 53),
('草莓蛋糕', 3, '新鲜草莓蛋糕', 'https://picsum.photos/400/400?random=54', 26.00, 23.40, 17.00, 50, 42, '热量：310kcal', 1, 0, 54),
('曲奇饼干', 3, '香脆曲奇', 'https://picsum.photos/400/400?random=55', 15.00, 13.50, 9.00, 100, 67, '热量：180kcal', 1, 0, 55),
('马卡龙', 3, '法式马卡龙', 'https://picsum.photos/400/400?random=56', 18.00, 16.20, 11.00, 80, 58, '热量：150kcal', 1, 1, 56),
('泡芙', 3, '奶油泡芙', 'https://picsum.photos/400/400?random=57', 12.00, 10.80, 7.00, 80, 71, '热量：200kcal', 1, 0, 57),
('蛋挞', 3, '葡式蛋挞', 'https://picsum.photos/400/400?random=58', 8.00, 7.20, 4.50, 100, 89, '热量：220kcal', 1, 1, 58);

-- ========================================
-- 更新商品图片为在线地址
-- ========================================
UPDATE `product` SET `image` = CONCAT('https://picsum.photos/400/400?random=', `id`) WHERE `id` >= 11;

-- ========================================
-- 统计信息
-- ========================================
SELECT '商品数据添加完成！' AS message;
SELECT CONCAT('奶茶类商品: ', COUNT(*)) AS count FROM product WHERE category_id = 4;
SELECT CONCAT('果茶类商品: ', COUNT(*)) AS count FROM product WHERE category_id = 5;
SELECT CONCAT('奶盖茶类商品: ', COUNT(*)) AS count FROM product WHERE category_id = 6;
SELECT CONCAT('咖啡类商品: ', COUNT(*)) AS count FROM product WHERE category_id = 2;
SELECT CONCAT('小食类商品: ', COUNT(*)) AS count FROM product WHERE category_id = 3;
SELECT CONCAT('商品总数: ', COUNT(*)) AS total FROM product WHERE deleted = 0;



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

-- 清理重复的营销活动数据
USE `milk_tea`;

-- 删除所有"新品抄茶"和"买一送一"活动
DELETE FROM `marketing_activity` WHERE `name` IN ('新品抄茶', '新品炒茶', '买一送一');

-- 查看剩余的活动
SELECT * FROM `marketing_activity` WHERE `status` = 1 AND `deleted` = 0;

-- 查询用户21的详细信息
USE `milk_tea`;

SELECT 
    id,
    username,
    nickname,
    member_level,
    points,
    balance,
    create_time
FROM user 
WHERE id = 21;

-- 如果要手动设置为钻石会员，需要给足够的积分
-- 方案1：直接更新积分为5000以上
UPDATE user 
SET points = 5000 
WHERE id = 21;

-- 方案2：直接更新会员等级（但下次积分变化时会被重新计算）
-- UPDATE user 
-- SET member_level = 2 
-- WHERE id = 21;

-- 验证更新结果
SELECT 
    id,
    username,
    nickname,
    member_level,
    points,
    balance
FROM user 
WHERE id = 21;



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


-- 步骤2: 为现有用户生成邀请码
USE milk_tea;

-- 为所有用户生成邀请码
UPDATE `user` 
SET `invite_code` = CONCAT(
    SUBSTRING('ABCDEFGHJKLMNPQRSTUVWXYZ23456789', FLOOR(1 + RAND() * 32), 1),
    SUBSTRING('ABCDEFGHJKLMNPQRSTUVWXYZ23456789', FLOOR(1 + RAND() * 32), 1),
    SUBSTRING('ABCDEFGHJKLMNPQRSTUVWXYZ23456789', FLOOR(1 + RAND() * 32), 1),
    SUBSTRING('ABCDEFGHJKLMNPQRSTUVWXYZ23456789', FLOOR(1 + RAND() * 32), 1),
    SUBSTRING('ABCDEFGHJKLMNPQRSTUVWXYZ23456789', FLOOR(1 + RAND() * 32), 1),
    SUBSTRING('ABCDEFGHJKLMNPQRSTUVWXYZ23456789', FLOOR(1 + RAND() * 32), 1)
)
WHERE `invite_code` IS NULL;

-- 查看结果
SELECT id, username, nickname, invite_code, inviter_id FROM `user` LIMIT 10;
