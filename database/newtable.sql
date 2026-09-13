-- ========================================
-- 奶茶小程序数据库设计 - 仅表结构
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
  `gender` int(11) DEFAULT NULL COMMENT '性别：1-男，2-女',
  `birthday` varchar(20) DEFAULT NULL COMMENT '生日',
  `openid` varchar(100) DEFAULT NULL COMMENT '微信openid',
  `member_level` int(11) DEFAULT '0' COMMENT '会员等级：0-普通会员，1-黄金会员，2-钻石会员',
  `member_no` varchar(50) DEFAULT NULL COMMENT '会员号',
  `invite_code` VARCHAR(20) DEFAULT NULL COMMENT '邀请码',
  `inviter_id` BIGINT(20) DEFAULT NULL COMMENT '邀请人ID',
  `invite_time` DATETIME DEFAULT NULL COMMENT '被邀请时间',
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
  UNIQUE KEY `uk_member_no` (`member_no`),
  UNIQUE KEY `uk_invite_code` (`invite_code`),
  KEY `idx_phone` (`phone`),
  KEY `idx_openid` (`openid`),
  KEY `idx_inviter_id` (`inviter_id`)
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
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
  `discount_amount` decimal(10,2) DEFAULT '0.00' COMMENT '优惠金额',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '实付金额',
  `actual_amount` decimal(10,2) NOT NULL COMMENT '实际支付金额',
  `pay_type` int(11) DEFAULT NULL COMMENT '支付方式：1-微信支付（沙箱）',
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
  `sweetness` int(11) DEFAULT NULL COMMENT '甜度：0-无糖，1-三分糖，2-五分糖，3-七分糖，4-正常',
  `temperature` int(11) DEFAULT NULL COMMENT '温度：0-去冰，1-少冰，2-正常冰，3-热',
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
-- 退款申请表
-- ========================================
DROP TABLE IF EXISTS `refund_request`;
CREATE TABLE `refund_request` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '退款申请ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `customer_name` varchar(50) DEFAULT NULL COMMENT '客户名称',
  `refund_amount` decimal(10,2) NOT NULL COMMENT '退款金额',
  `reason` varchar(500) NOT NULL COMMENT '退款原因',
  `status` int(11) DEFAULT '0' COMMENT '状态：0-待处理，1-已同意，2-已拒绝',
  `reject_reason` varchar(500) DEFAULT NULL COMMENT '拒绝原因',
  `process_time` datetime DEFAULT NULL COMMENT '处理时间',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款申请表';

-- ========================================
-- 投诉表
-- ========================================
DROP TABLE IF EXISTS `complaint`;
CREATE TABLE `complaint` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '投诉ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `customer_name` varchar(50) DEFAULT NULL COMMENT '客户名称',
  `complaint_type` int(11) NOT NULL COMMENT '投诉类型：1-商品质量，2-服务态度，3-配送问题，4-其他',
  `content` varchar(1000) NOT NULL COMMENT '投诉内容',
  `images` varchar(500) DEFAULT NULL COMMENT '图片（JSON格式）',
  `status` int(11) DEFAULT '0' COMMENT '状态：0-待处理，1-处理中，2-已解决，3-已关闭',
  `response` varchar(1000) DEFAULT NULL COMMENT '处理回复',
  `process_time` datetime DEFAULT NULL COMMENT '处理时间',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投诉表';

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

-- ========================================
-- 营销活动表
-- ========================================
DROP TABLE IF EXISTS `marketing_activity`;
CREATE TABLE `marketing_activity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `name` varchar(100) NOT NULL COMMENT '活动名称',
  `type` varchar(20) NOT NULL COMMENT '活动类型：discount-满减，seckill-秒杀，gift-买赠，member-会员专享',
  `description` varchar(500) DEFAULT NULL COMMENT '活动描述',
  `rules` text COMMENT '活动规则',
  `banner` varchar(255) DEFAULT NULL COMMENT '活动横幅图片',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `participants` int(11) DEFAULT '0' COMMENT '参与人数',
  `revenue` decimal(10,2) DEFAULT '0.00' COMMENT '活动收益',
  `status` int(11) DEFAULT '0' COMMENT '状态：0-未开始，1-进行中，2-已结束，3-已暂停',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_end_time` (`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营销活动表';

-- ========================================
-- 打印设备表
-- ========================================
DROP TABLE IF EXISTS `print_device`;
CREATE TABLE `print_device` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '设备ID',
  `name` varchar(100) NOT NULL COMMENT '设备名称',
  `type` varchar(20) NOT NULL COMMENT '设备类型：thermal-热敏，dot-针式，laser-激光',
  `connection` varchar(20) NOT NULL COMMENT '连接方式：network-网络，usb-USB，bluetooth-蓝牙',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `port` int(11) DEFAULT NULL COMMENT '端口',
  `path` varchar(255) DEFAULT NULL COMMENT 'USB设备路径',
  `mac` varchar(50) DEFAULT NULL COMMENT '蓝牙MAC地址',
  `status` int(11) DEFAULT '1' COMMENT '状态：0-离线，1-在线',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打印设备表';

-- ========================================
-- 打印模板表
-- ========================================
DROP TABLE IF EXISTS `print_template`;
CREATE TABLE `print_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `name` varchar(100) NOT NULL COMMENT '模板名称',
  `type` varchar(20) NOT NULL COMMENT '模板类型：order-订单小票，kitchen-厨房单，delivery-配送单',
  `width` int(11) NOT NULL COMMENT '纸张宽度(mm)',
  `content` text NOT NULL COMMENT '模板内容',
  `header` varchar(500) DEFAULT NULL COMMENT '页眉',
  `footer` varchar(500) DEFAULT NULL COMMENT '页脚',
  `is_default` int(11) DEFAULT '0' COMMENT '是否默认：0-否，1-是',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打印模板表';

-- ========================================
-- 打印记录表
-- ========================================
DROP TABLE IF EXISTS `print_record`;
CREATE TABLE `print_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `device_id` bigint(20) NOT NULL COMMENT '设备ID',
  `template_id` bigint(20) NOT NULL COMMENT '模板ID',
  `status` int(11) DEFAULT '1' COMMENT '状态：1-成功，2-失败，3-处理中',
  `error_message` varchar(500) DEFAULT NULL COMMENT '错误信息',
  `copies` int(11) DEFAULT '1' COMMENT '打印份数',
  `duration` int(11) DEFAULT NULL COMMENT '打印耗时(秒)',
  `retry_count` int(11) DEFAULT '0' COMMENT '重试次数',
  `print_time` datetime DEFAULT NULL COMMENT '打印时间',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_device_id` (`device_id`),
  KEY `idx_template_id` (`template_id`),
  CONSTRAINT `fk_print_record_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `fk_print_record_device` FOREIGN KEY (`device_id`) REFERENCES `print_device` (`id`),
  CONSTRAINT `fk_print_record_template` FOREIGN KEY (`template_id`) REFERENCES `print_template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打印记录表';

-- ========================================
-- 员工表
-- ========================================
DROP TABLE IF EXISTS `staff`;
CREATE TABLE `staff` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '员工ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码（MD5加密）',
  `name` varchar(50) NOT NULL COMMENT '员工姓名',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `employee_id` varchar(50) NOT NULL COMMENT '工号',
  `role` varchar(50) NOT NULL DEFAULT 'cashier' COMMENT '角色：super_admin-超级管理员，manager-店长，cashier-收银员，maker-制作员',
  `department` varchar(50) DEFAULT NULL COMMENT '部门：management-管理部，front-前台部，kitchen-制作部，delivery-配送部',
  `hire_date` date DEFAULT NULL COMMENT '入职时间',
  `salary` decimal(10,2) DEFAULT '0.00' COMMENT '薪资',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `permissions` text COMMENT '权限（JSON格式）',
  `status` int(11) DEFAULT '1' COMMENT '状态：0-离职，1-在职',
  `last_login` datetime DEFAULT NULL COMMENT '最后登录时间',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_employee_id` (`employee_id`),
  KEY `idx_phone` (`phone`),
  KEY `idx_role` (`role`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- ========================================
-- 门店表
-- ========================================
DROP TABLE IF EXISTS `store`;
CREATE TABLE `store` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '门店ID',
  `name` varchar(100) NOT NULL COMMENT '门店名称',
  `address` varchar(255) NOT NULL COMMENT '门店地址',
  `phone` varchar(20) NOT NULL COMMENT '联系电话',
  `manager` varchar(50) DEFAULT NULL COMMENT '店长姓名',
  `manager_phone` varchar(20) DEFAULT NULL COMMENT '店长电话',
  `business_hours` varchar(100) DEFAULT NULL COMMENT '营业时间（JSON格式，如：["09:00","22:00"]）',
  `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度',
  `area` decimal(10,2) DEFAULT NULL COMMENT '门店面积（平方米）',
  `staff_count` int(11) DEFAULT '0' COMMENT '员工数量',
  `description` text COMMENT '门店描述',
  `images` text COMMENT '门店图片（JSON格式）',
  `status` int(11) DEFAULT '1' COMMENT '状态：0-关闭，1-营业',
  `deleted` int(11) DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门店表';

-- ========================================
-- 系统配置表
-- ========================================
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(100) NOT NULL COMMENT '配置键',
  `config_value` text COMMENT '配置值',
  `description` varchar(255) DEFAULT NULL COMMENT '配置描述',
  `type` int(11) DEFAULT '1' COMMENT '配置类型：1-文本，2-数字，3-布尔，4-JSON',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- ========================================
-- 积分商品表
-- ========================================
DROP TABLE IF EXISTS `points_product`;
CREATE TABLE `points_product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '商品名称',
  `description` varchar(500) DEFAULT NULL COMMENT '商品描述',
  `image` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `points` int NOT NULL COMMENT '所需积分',
  `stock` int NOT NULL DEFAULT '0' COMMENT '库存数量',
  `exchanged` int NOT NULL DEFAULT '0' COMMENT '已兑换数量',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-下架，1-上架',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分商品表';

-- ========================================
-- 积分历史表
-- ========================================
DROP TABLE IF EXISTS `points_history`;
CREATE TABLE `points_history` (
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

-- ========================================
-- 商品收藏表
-- ========================================
DROP TABLE IF EXISTS `product_collection`;
CREATE TABLE `product_collection` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品收藏表';

-- ========================================
-- 登录记录表
-- ========================================
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

-- ========================================
-- 签到记录表
-- ========================================
DROP TABLE IF EXISTS `checkin_record`;
CREATE TABLE `checkin_record` (
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

-- ========================================
-- 邀请记录表
-- ========================================
DROP TABLE IF EXISTS `invite_record`;
CREATE TABLE `invite_record` (
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

-- ========================================
-- 用户反馈表
-- ========================================
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback` (
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

-- ========================================
-- 用户设置表
-- ========================================
DROP TABLE IF EXISTS `user_settings`;
CREATE TABLE `user_settings` (
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
