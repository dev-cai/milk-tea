/*
 Navicat Premium Dump SQL

 Source Server         : root
 Source Server Type    : MySQL
 Source Server Version : 80012 (8.0.12)
 Source Host           : localhost:3306
 Source Schema         : milk_tea

 Target Server Type    : MySQL
 Target Server Version : 80012 (8.0.12)
 File Encoding         : 65001

 Date: 27/11/2025 08:31:29
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for banner
-- ----------------------------
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片地址',
  `link` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '跳转链接',
  `sort` int(11) NULL DEFAULT 0 COMMENT '排序',
  `status` int(11) NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `deleted` int(11) NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '轮播图表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of banner
-- ----------------------------
INSERT INTO `banner` VALUES (1, '新品上市', 'https://picsum.photos/750/300?random=11', NULL, 1, 1, 0, '2025-11-19 18:18:32', '2025-11-26 10:50:53');
INSERT INTO `banner` VALUES (2, '限时特惠', 'https://picsum.photos/750/300?random=12', NULL, 2, 1, 0, '2025-11-19 18:18:32', '2025-11-26 10:50:53');
INSERT INTO `banner` VALUES (3, '会员专享', 'https://picsum.photos/750/300?random=13', NULL, 3, 1, 0, '2025-11-19 18:18:32', '2025-11-26 10:50:53');

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父级分类ID，0表示一级分类',
  `sort` int(11) NULL DEFAULT 0 COMMENT '排序',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类图标',
  `status` int(11) NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `deleted` int(11) NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, '茶饮', 0, 1, NULL, 1, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `category` VALUES (2, '咖啡', 0, 2, NULL, 1, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `category` VALUES (3, '小食', 0, 3, NULL, 1, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `category` VALUES (4, '奶茶', 1, 1, NULL, 1, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `category` VALUES (5, '果茶', 1, 2, NULL, 1, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `category` VALUES (6, '奶盖茶', 1, 3, NULL, 1, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');

-- ----------------------------
-- Table structure for checkin_record
-- ----------------------------
DROP TABLE IF EXISTS `checkin_record`;
CREATE TABLE `checkin_record`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `checkin_date` date NOT NULL COMMENT '签到日期',
  `continuous_days` int(11) NULL DEFAULT 1 COMMENT '连续签到天数',
  `points` int(11) NULL DEFAULT 10 COMMENT '获得积分',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_date`(`user_id` ASC, `checkin_date` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '签到记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of checkin_record
-- ----------------------------
INSERT INTO `checkin_record` VALUES (1, 2, '2024-11-21', 1, 10, '2025-11-26 17:15:16');
INSERT INTO `checkin_record` VALUES (2, 2, '2024-11-22', 2, 10, '2025-11-26 17:15:16');
INSERT INTO `checkin_record` VALUES (3, 2, '2024-11-23', 3, 10, '2025-11-26 17:15:16');

-- ----------------------------
-- Table structure for complaint
-- ----------------------------
DROP TABLE IF EXISTS `complaint`;
CREATE TABLE `complaint`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '投诉ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `customer_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '客户名称',
  `complaint_type` int(11) NOT NULL COMMENT '投诉类型：1-商品质量，2-服务态度，3-配送问题，4-其他',
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '投诉内容',
  `images` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片（JSON格式）',
  `status` int(11) NULL DEFAULT 0 COMMENT '状态：0-待处理，1-处理中，2-已解决，3-已关闭',
  `response` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理回复',
  `process_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `deleted` int(11) NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '投诉表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of complaint
-- ----------------------------
INSERT INTO `complaint` VALUES (1, 1, 'ORD202411190001', 2, '张三', 1, '奶茶里面有异物，怀疑是制作过程中混入的杂质，希望能够重视食品安全问题', NULL, 0, NULL, NULL, 0, '2024-11-19 10:00:00', '2025-11-19 18:18:32');
INSERT INTO `complaint` VALUES (2, 2, 'ORD202411190002', 3, '李四', 2, '店员态度很差，说话不礼貌，服务态度需要改进', NULL, 1, '非常抱歉给您带来不好的体验，我们已经对相关员工进行了培训', '2024-11-19 11:00:00', 0, '2024-11-19 10:30:00', '2025-11-19 18:18:32');
INSERT INTO `complaint` VALUES (3, 3, 'ORD202411190003', 2, '张三', 3, '配送时间太长，等了40分钟才送到，奶茶都凉了', NULL, 2, '非常抱歉，由于当时订单量较大导致配送延迟，我们已经优化了配送流程，并为您补偿了一张优惠券', '2024-11-19 12:30:00', 0, '2024-11-19 12:00:00', '2025-11-19 18:18:32');
INSERT INTO `complaint` VALUES (4, 4, 'ORD202411190004', 3, '李四', 1, '珍珠煮的太硬了，口感很差', NULL, 0, NULL, NULL, 0, '2024-11-19 12:30:00', '2025-11-19 18:18:32');
INSERT INTO `complaint` VALUES (5, 7, 'ORD202411190007', 2, '张三', 4, '包装破损，奶茶洒了一半', NULL, 3, '已为您重新制作并配送，同时赠送了小食作为补偿', '2024-11-19 15:45:00', 0, '2024-11-19 15:30:00', '2025-11-19 18:18:32');
INSERT INTO `complaint` VALUES (6, 8, 'ORD202411190008', 3, '李四', 1, '芝士奶盖不新鲜，有异味', NULL, 0, NULL, NULL, 0, '2024-11-19 16:50:00', '2025-11-19 18:18:32');

-- ----------------------------
-- Table structure for coupon
-- ----------------------------
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '优惠券名称',
  `type` int(11) NOT NULL COMMENT '优惠券类型：1-满减券，2-折扣券，3-兑换券',
  `discount` decimal(10, 2) NOT NULL COMMENT '折扣金额或折扣率',
  `min_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '最低消费金额',
  `total_count` int(11) NOT NULL COMMENT '发行数量',
  `received_count` int(11) NULL DEFAULT 0 COMMENT '已领取数量',
  `valid_start` datetime NOT NULL COMMENT '有效期开始',
  `valid_end` datetime NOT NULL COMMENT '有效期结束',
  `status` int(11) NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `deleted` int(11) NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '优惠券表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of coupon
-- ----------------------------
INSERT INTO `coupon` VALUES (1, '新用户专享券', 1, 5.00, 20.00, 1000, 1, '2024-01-01 00:00:00', '2025-12-31 23:59:59', 1, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `coupon` VALUES (2, '满30减10', 1, 10.00, 30.00, 500, 1, '2024-01-01 00:00:00', '2025-12-31 23:59:59', 1, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `coupon` VALUES (3, '8折优惠券', 2, 0.80, 0.00, 300, 1, '2024-01-01 00:00:00', '2025-12-31 23:59:59', 1, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');

-- ----------------------------
-- Table structure for feedback
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
  `type` int(11) NOT NULL COMMENT '反馈类型：1-产品问题 2-服务问题 3-配送问题 4-支付问题 5-功能建议 6-其他问题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '反馈内容',
  `images` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片地址，多张图片用逗号分隔',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `contact_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系邮箱',
  `status` int(11) NULL DEFAULT 0 COMMENT '处理状态：0-待处理 1-处理中 2-已回复',
  `reply` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '客服回复内容',
  `reply_time` datetime NULL DEFAULT NULL COMMENT '回复时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户反馈表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of feedback
-- ----------------------------
INSERT INTO `feedback` VALUES (1, 21, 1, ' --- compiler:3.11.0:compile (default-compile) @ milk-tea-backend ---\n[INFO] Changes detected - recompiling the module! :source\n[INFO] Compiling 131 source files with javac [debug release 17] to target\\classes\n[INFO] /D:/Project/Java_Project/javaweb/milk-tea-backend/src/main/java/com/milktea/service/RecipeService.java: D:\\Project\\Java_Project\\javaweb\\milk-tea-backend\\src\\main\\java\\com\\milktea\\service\\RecipeService.java使用或覆盖了已过时的 API。\n[INFO] /D:/Project/Java_Project/javaweb/milk-tea-backend/src/m', 'http://tmp/1LxnxiKSgHS5d6604709b04153bec3ccb7f8efa89057.png', '15045678901', '', 0, NULL, NULL, '2025-11-26 20:03:47', '2025-11-26 20:03:47');

-- ----------------------------
-- Table structure for invite_record
-- ----------------------------
DROP TABLE IF EXISTS `invite_record`;
CREATE TABLE `invite_record`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `inviter_id` bigint(20) NOT NULL COMMENT '邀请人ID',
  `invitee_id` bigint(20) NOT NULL COMMENT '被邀请人ID',
  `status` int(11) NULL DEFAULT 0 COMMENT '状态：0-待注册，1-已注册，2-已奖励',
  `points` int(11) NULL DEFAULT 50 COMMENT '奖励积分',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `reward_time` datetime NULL DEFAULT NULL COMMENT '奖励时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_inviter_id`(`inviter_id` ASC) USING BTREE,
  INDEX `idx_invitee_id`(`invitee_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '邀请记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of invite_record
-- ----------------------------
INSERT INTO `invite_record` VALUES (1, 2, 3, 2, 50, '2024-11-22 14:20:00', '2024-11-22 14:21:00');

-- ----------------------------
-- Table structure for login_history
-- ----------------------------
DROP TABLE IF EXISTS `login_history`;
CREATE TABLE `login_history`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `device` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登录设备',
  `device_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备类型：ios/android/web/miniapp',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登录地点',
  `login_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_login_time`(`login_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '登录记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of login_history
-- ----------------------------
INSERT INTO `login_history` VALUES (1, 2, 'iPhone 13', 'ios', '192.168.1.100', '广东省深圳市', '2024-11-26 14:30:25');
INSERT INTO `login_history` VALUES (2, 2, 'Android', 'android', '192.168.1.101', '广东省深圳市', '2024-11-25 09:15:10');
INSERT INTO `login_history` VALUES (3, 2, '微信小程序', 'miniapp', '192.168.1.102', '广东省深圳市', '2024-11-24 20:45:33');
INSERT INTO `login_history` VALUES (4, 3, 'iPhone 12', 'ios', '192.168.1.103', '北京市朝阳区', '2024-11-26 10:20:15');
INSERT INTO `login_history` VALUES (5, 3, 'Web浏览器', 'web', '192.168.1.104', '北京市朝阳区', '2024-11-25 16:30:00');

-- ----------------------------
-- Table structure for marketing_activity
-- ----------------------------
DROP TABLE IF EXISTS `marketing_activity`;
CREATE TABLE `marketing_activity`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '活动名称',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '活动类型：discount-满减，seckill-秒杀，gift-买赠，member-会员专享',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '活动图标',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '活动描述',
  `rules` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '活动规则',
  `banner` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '活动横幅图片',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `participants` int(11) NULL DEFAULT 0 COMMENT '参与人数',
  `revenue` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '活动收益',
  `status` int(11) NULL DEFAULT 0 COMMENT '状态：0-未开始，1-进行中，2-已结束，3-已暂停',
  `deleted` int(11) NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_start_time`(`start_time` ASC) USING BTREE,
  INDEX `idx_end_time`(`end_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '营销活动表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of marketing_activity
-- ----------------------------
INSERT INTO `marketing_activity` VALUES (1, '双11狂欢节', 'discount', NULL, '全场满50减10，满100减25', '活动期间，单笔订单满50元减10元，满100元减25元', '/images/activity1.jpg', '2024-11-11 00:00:00', '2024-11-11 23:59:59', 580, 15600.00, 2, 0, '2025-11-20 09:22:57', '2025-11-20 09:22:57');
INSERT INTO `marketing_activity` VALUES (2, '新品秒杀', 'seckill', NULL, '新品限时秒杀，每日10点开抢', '每日10:00-12:00，指定新品5折秒杀，限量100份', '/images/activity2.jpg', '2024-11-15 00:00:00', '2024-11-30 23:59:59', 320, 8900.00, 1, 0, '2025-11-20 09:22:57', '2025-11-20 09:22:57');
INSERT INTO `marketing_activity` VALUES (4, '会员日', 'member', NULL, '会员专享8折优惠', '每周三会员日，全场商品会员8折', '/images/activity4.jpg', '2024-11-01 00:00:00', '2024-12-31 23:59:59', 450, 12800.00, 1, 0, '2025-11-20 09:22:57', '2025-11-20 09:22:57');
INSERT INTO `marketing_activity` VALUES (5, '圣诞特惠', 'discount', NULL, '圣诞节满减活动', '圣诞节期间，满88减18，满128减38', '/images/activity5.jpg', '2024-12-24 00:00:00', '2024-12-26 23:59:59', 0, 0.00, 0, 0, '2025-11-20 09:22:57', '2025-11-20 09:22:57');
INSERT INTO `marketing_activity` VALUES (6, '新品抄茶', 'new_product', '🆕', '全新口味上线，限时尝鲜', '新品首发，前100名享8折优惠', '', '2025-11-26 20:38:52', '2025-12-26 20:38:52', 0, 0.00, 1, 0, '2025-11-26 20:38:52', '2025-11-26 20:38:52');
INSERT INTO `marketing_activity` VALUES (7, '买一送一', 'buy_one_get_one', '🎁', '指定商品买一送一', '购买指定商品即可享受买一送一优惠，每人每天限参与一次', '', '2025-11-26 20:38:52', '2025-12-03 20:38:52', 0, 0.00, 1, 0, '2025-11-26 20:38:52', '2025-11-26 20:38:52');

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单明细ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `product_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品图片',
  `price` decimal(10, 2) NOT NULL COMMENT '单价',
  `quantity` int(11) NOT NULL COMMENT '数量',
  `sweetness` int(11) NULL DEFAULT NULL COMMENT '甜度：0-无糖，1-三分，2-五分，3-七分，4-正常',
  `temperature` int(11) NULL DEFAULT NULL COMMENT '温度：0-去冰，1-少冰，2-正常，3-热',
  `toppings` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '加料（JSON格式）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 63 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (1, 1, 1, '珍珠奶茶', '/api/uploads/images/sample/product1.jpg', 12.00, 1, 2, 1, '[\"珍珠\"]', '2025-11-19 18:18:32');
INSERT INTO `order_item` VALUES (2, 1, 9, '拿铁咖啡', '/api/uploads/images/sample/product9.jpg', 18.00, 1, 3, 3, '[]', '2025-11-19 18:18:32');
INSERT INTO `order_item` VALUES (3, 2, 9, '拿铁咖啡', '/api/uploads/images/sample/product9.jpg', 18.00, 1, 3, 3, '[]', '2025-11-19 18:18:32');
INSERT INTO `order_item` VALUES (4, 3, 2, '波霸奶茶', '/api/uploads/images/sample/product2.jpg', 13.00, 1, 2, 2, '[\"波霸\"]', '2025-11-19 18:18:32');
INSERT INTO `order_item` VALUES (5, 3, 9, '拿铁咖啡', '/api/uploads/images/sample/product9.jpg', 18.00, 1, 4, 3, '[]', '2025-11-19 18:18:32');
INSERT INTO `order_item` VALUES (6, 4, 1, '珍珠奶茶', '/api/uploads/images/sample/product1.jpg', 12.00, 1, 1, 1, '[\"珍珠\"]', '2025-11-19 18:18:32');
INSERT INTO `order_item` VALUES (7, 5, 3, '红豆奶茶', '/api/uploads/images/sample/product3.jpg', 14.00, 1, 2, 2, '[\"红豆\"]', '2025-11-19 18:18:32');
INSERT INTO `order_item` VALUES (8, 5, 7, '芝士奶盖茶', '/api/uploads/images/sample/product7.jpg', 18.00, 1, 3, 1, '[\"芝士奶盖\"]', '2025-11-19 18:18:32');
INSERT INTO `order_item` VALUES (9, 5, 10, '芝士蛋糕', '/api/uploads/images/sample/product10.jpg', 25.00, 1, 0, 0, '[]', '2025-11-19 18:18:32');
INSERT INTO `order_item` VALUES (10, 6, 8, '美式咖啡', '/api/uploads/images/sample/product8.jpg', 15.00, 1, 0, 3, '[]', '2025-11-19 18:18:32');
INSERT INTO `order_item` VALUES (11, 7, 4, '芋泥奶茶', '/api/uploads/images/sample/product4.jpg', 15.00, 1, 2, 2, '[\"芋泥\"]', '2025-11-19 18:18:32');
INSERT INTO `order_item` VALUES (12, 7, 6, '柠檬茶', '/api/uploads/images/sample/product6.jpg', 14.00, 1, 1, 1, '[\"柠檬片\", \"薄荷叶\"]', '2025-11-19 18:18:32');
INSERT INTO `order_item` VALUES (13, 8, 5, '百香果茶', '/api/uploads/images/sample/product5.jpg', 16.00, 1, 2, 1, '[\"百香果浆\"]', '2025-11-19 18:18:32');
INSERT INTO `order_item` VALUES (14, 8, 7, '芝士奶盖茶', '/api/uploads/images/sample/product7.jpg', 18.00, 1, 3, 2, '[\"芝士奶盖\"]', '2025-11-19 18:18:32');
INSERT INTO `order_item` VALUES (15, 9, 1, '珍珠奶茶', '/api/uploads/images/sample/product1.jpg', 12.00, 2, 2, 1, '[\"珍珠\"]', '2025-11-19 18:22:58');
INSERT INTO `order_item` VALUES (16, 9, 4, '芋泥奶茶', '/api/uploads/images/sample/product4.jpg', 15.00, 1, 3, 2, '[\"芋泥\"]', '2025-11-19 18:22:58');
INSERT INTO `order_item` VALUES (17, 10, 2, '波霸奶茶', '/api/uploads/images/sample/product2.jpg', 13.00, 2, 2, 1, '[\"波霸\"]', '2025-11-19 18:22:58');
INSERT INTO `order_item` VALUES (18, 10, 1, '珍珠奶茶', '/api/uploads/images/sample/product1.jpg', 12.00, 1, 1, 1, '[\"珍珠\"]', '2025-11-19 18:22:58');
INSERT INTO `order_item` VALUES (19, 11, 7, '芝士奶盖茶', '/api/uploads/images/sample/product7.jpg', 18.00, 2, 3, 1, '[\"芝士奶盖\"]', '2025-11-19 18:22:58');
INSERT INTO `order_item` VALUES (20, 11, 5, '百香果茶', '/api/uploads/images/sample/product5.jpg', 16.00, 1, 2, 1, '[\"百香果浆\"]', '2025-11-19 18:22:58');
INSERT INTO `order_item` VALUES (21, 12, 3, '红豆奶茶', '/api/uploads/images/sample/product3.jpg', 14.00, 2, 2, 2, '[\"红豆\"]', '2025-11-19 18:22:58');
INSERT INTO `order_item` VALUES (22, 12, 1, '珍珠奶茶', '/api/uploads/images/sample/product1.jpg', 12.00, 1, 1, 1, '[\"珍珠\"]', '2025-11-19 18:22:58');
INSERT INTO `order_item` VALUES (23, 13, 7, '芝士奶盖茶', '/api/uploads/images/sample/product7.jpg', 18.00, 3, 3, 1, '[\"芝士奶盖\"]', '2025-11-19 18:22:58');
INSERT INTO `order_item` VALUES (24, 13, 10, '芝士蛋糕', '/api/uploads/images/sample/product10.jpg', 25.00, 1, 0, 0, '[]', '2025-11-19 18:22:58');
INSERT INTO `order_item` VALUES (25, 14, 4, '芋泥奶茶', '/api/uploads/images/sample/product4.jpg', 15.00, 4, 3, 2, '[\"芋泥\"]', '2025-11-19 18:22:58');
INSERT INTO `order_item` VALUES (26, 14, 5, '百香果茶', '/api/uploads/images/sample/product5.jpg', 16.00, 2, 2, 1, '[\"百香果浆\"]', '2025-11-19 18:22:58');
INSERT INTO `order_item` VALUES (27, 15, 9, '拿铁咖啡', '/api/uploads/images/sample/product9.jpg', 18.00, 4, 4, 3, '[]', '2025-11-19 18:22:58');
INSERT INTO `order_item` VALUES (28, 15, 10, '芝士蛋糕', '/api/uploads/images/sample/product10.jpg', 25.00, 1, 0, 0, '[]', '2025-11-19 18:22:58');
INSERT INTO `order_item` VALUES (29, 25, 7, '芝士奶盖茶', '/api/uploads/images/sample/product7.jpg', 18.00, 1, 4, 2, '', '2025-11-20 12:23:51');
INSERT INTO `order_item` VALUES (30, 26, 7, '芝士奶盖茶', '/api/uploads/images/sample/product7.jpg', 18.00, 1, 4, 2, '', '2025-11-20 12:23:54');
INSERT INTO `order_item` VALUES (31, 27, 7, '芝士奶盖茶', '/api/uploads/images/sample/product7.jpg', 18.00, 1, 4, 2, '', '2025-11-20 12:23:59');
INSERT INTO `order_item` VALUES (32, 28, 7, '芝士奶盖茶', '/api/uploads/images/sample/product7.jpg', 18.00, 1, 4, 2, '', '2025-11-20 12:24:00');
INSERT INTO `order_item` VALUES (33, 29, 7, '芝士奶盖茶', '/api/uploads/images/sample/product7.jpg', 18.00, 1, 4, 2, '', '2025-11-20 12:24:01');
INSERT INTO `order_item` VALUES (34, 30, 7, '芝士奶盖茶', '/api/uploads/images/sample/product7.jpg', 18.00, 1, 4, 2, '', '2025-11-20 12:24:02');
INSERT INTO `order_item` VALUES (35, 31, 2, '波霸奶茶', '/api/uploads/images/sample/product2.jpg', 13.00, 1, 4, 2, '', '2025-11-20 12:25:16');
INSERT INTO `order_item` VALUES (36, 32, 2, '波霸奶茶', '/api/uploads/images/sample/product2.jpg', 13.00, 1, 4, 2, '', '2025-11-20 12:25:20');
INSERT INTO `order_item` VALUES (37, 33, 2, '波霸奶茶', '/api/uploads/images/sample/product2.jpg', 13.00, 1, 4, 2, '', '2025-11-20 12:25:22');
INSERT INTO `order_item` VALUES (38, 34, 2, '波霸奶茶', '/api/uploads/images/sample/product2.jpg', 13.00, 1, 4, 2, '', '2025-11-20 12:28:31');
INSERT INTO `order_item` VALUES (39, 35, 4, '芋泥奶茶', '/api/uploads/images/sample/product4.jpg', 15.00, 1, 4, 2, '', '2025-11-20 12:54:40');
INSERT INTO `order_item` VALUES (40, 36, 1, '珍珠奶茶', '/api/uploads/images/sample/product1.jpg', 12.00, 3, 4, 2, '', '2025-11-20 12:55:12');
INSERT INTO `order_item` VALUES (41, 37, 1, '珍珠奶茶', '/api/uploads/images/sample/product1.jpg', 12.00, 3, 4, 2, '', '2025-11-20 12:55:23');
INSERT INTO `order_item` VALUES (42, 38, 1, '珍珠奶茶', '/api/uploads/images/sample/product1.jpg', 12.00, 3, 4, 2, '', '2025-11-20 12:55:38');
INSERT INTO `order_item` VALUES (43, 39, 1, '珍珠奶茶', '/api/uploads/images/sample/product1.jpg', 12.00, 3, 4, 2, '', '2025-11-20 12:56:16');
INSERT INTO `order_item` VALUES (44, 40, 1, '珍珠奶茶', '/api/uploads/images/sample/product1.jpg', 12.00, 3, 4, 2, '', '2025-11-20 12:59:29');
INSERT INTO `order_item` VALUES (45, 41, 1, '珍珠奶茶', '/api/uploads/images/sample/product1.jpg', 12.00, 3, 4, 2, '', '2025-11-20 13:02:41');
INSERT INTO `order_item` VALUES (46, 42, 1, '珍珠奶茶', '/api/uploads/images/sample/product1.jpg', 12.00, 1, 4, 2, '', '2025-11-20 14:10:12');
INSERT INTO `order_item` VALUES (47, 43, 1, '珍珠奶茶', '/api/uploads/images/sample/product1.jpg', 12.00, 1, 4, 2, '', '2025-11-20 14:28:19');
INSERT INTO `order_item` VALUES (48, 44, 2, '波霸奶茶', '/api/uploads/images/sample/product2.jpg', 13.00, 1, 4, 2, '', '2025-11-20 14:33:50');
INSERT INTO `order_item` VALUES (49, 45, 1, '珍珠奶茶', '/api/uploads/images/sample/product1.jpg', 12.00, 1, 4, 2, '', '2025-11-20 14:36:14');
INSERT INTO `order_item` VALUES (50, 46, 5, '百香果茶', '/api/uploads/images/sample/product5.jpg', 16.00, 1, 4, 2, '', '2025-11-20 14:39:08');
INSERT INTO `order_item` VALUES (51, 47, 5, '百香果茶', '/api/uploads/images/sample/product5.jpg', 16.00, 1, 4, 2, '', '2025-11-20 14:39:26');
INSERT INTO `order_item` VALUES (52, 48, 5, '百香果茶', '/api/uploads/images/sample/product5.jpg', 16.00, 1, 4, 2, '', '2025-11-20 14:39:48');
INSERT INTO `order_item` VALUES (53, 49, 5, '百香果茶', '/api/uploads/images/sample/product5.jpg', 16.00, 1, 4, 2, '', '2025-11-20 14:39:52');
INSERT INTO `order_item` VALUES (54, 50, 5, '百香果茶', '/api/uploads/images/sample/product5.jpg', 16.00, 1, 4, 2, '', '2025-11-20 14:39:55');
INSERT INTO `order_item` VALUES (55, 51, 5, '百香果茶', '/api/uploads/images/sample/product5.jpg', 16.00, 1, 4, 2, '', '2025-11-20 14:39:58');
INSERT INTO `order_item` VALUES (56, 52, 4, '芋泥奶茶', '/api/uploads/images/2025/11/20/96060f81-5c18-44ec-a235-012248019fd2.jpg', 15.00, 1, 4, 2, '', '2025-11-20 14:42:26');
INSERT INTO `order_item` VALUES (57, 53, 4, '芋泥奶茶', '/api/uploads/images/2025/11/20/96060f81-5c18-44ec-a235-012248019fd2.jpg', 15.00, 1, 4, 2, '', '2025-11-20 14:43:42');
INSERT INTO `order_item` VALUES (58, 54, 4, '芋泥奶茶', '/api/uploads/images/2025/11/20/96060f81-5c18-44ec-a235-012248019fd2.jpg', 15.00, 1, 4, 2, '', '2025-11-20 14:45:54');
INSERT INTO `order_item` VALUES (59, 55, 4, '芋泥奶茶', '/api/uploads/images/2025/11/20/96060f81-5c18-44ec-a235-012248019fd2.jpg', 15.00, 1, 4, 2, '', '2025-11-20 14:49:29');
INSERT INTO `order_item` VALUES (60, 56, 1, '珍珠奶茶', 'https://picsum.photos/400/400?random=1', 12.00, 1, 4, 2, '', '2025-11-26 11:01:40');
INSERT INTO `order_item` VALUES (61, 56, 4, '芋泥奶茶', 'https://picsum.photos/400/400?random=4', 15.00, 1, 4, 2, '', '2025-11-26 11:01:40');
INSERT INTO `order_item` VALUES (62, 57, 2, '波霸奶茶', 'https://picsum.photos/400/400?random=2', 13.00, 1, 4, 2, '', '2025-11-26 15:29:19');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `discount_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '优惠金额',
  `pay_amount` decimal(10, 2) NOT NULL COMMENT '实付金额',
  `actual_amount` decimal(10, 2) NOT NULL COMMENT '实际支付金额',
  `pay_type` int(11) NULL DEFAULT NULL COMMENT '支付方式：1-微信支付，2-余额支付，3-组合支付',
  `status` int(11) NULL DEFAULT 0 COMMENT '订单状态：0-待支付，1-待制作，2-制作中，3-待取餐，4-已完成，5-已取消，6-退款中，7-已退款',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `refund_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '退款原因',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `finish_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `cancel_time` datetime NULL DEFAULT NULL COMMENT '取消时间',
  `deleted` int(11) NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 58 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 'ORD202411190001', 2, '张三', 25.00, 3.00, 22.00, 22.00, 1, 4, '少糖少冰', NULL, '2024-11-19 09:30:00', NULL, NULL, 0, '2024-11-19 09:25:00', '2025-11-19 18:18:32');
INSERT INTO `orders` VALUES (2, 'ORD202411190002', 3, '李四', 18.00, 0.00, 18.00, 18.00, 2, 4, '正常甜度', NULL, '2024-11-19 10:15:00', '2025-11-26 11:05:10', NULL, 0, '2024-11-19 10:10:00', '2025-11-19 18:18:32');
INSERT INTO `orders` VALUES (3, 'ORD202411190003', 2, '张三', 31.00, 5.00, 26.00, 26.00, 1, 4, '热饮', NULL, '2024-11-19 11:20:00', '2025-11-26 11:05:09', NULL, 0, '2024-11-19 11:15:00', '2025-11-19 18:18:32');
INSERT INTO `orders` VALUES (4, 'ORD202411190004', 3, '李四', 12.00, 0.00, 12.00, 12.00, 1, 4, '', NULL, '2024-11-19 12:05:00', '2025-11-26 11:05:09', NULL, 0, '2024-11-19 12:00:00', '2025-11-19 18:18:32');
INSERT INTO `orders` VALUES (5, 'ORD202411190005', 2, '张三', 42.00, 8.00, 34.00, 34.00, 3, 4, '加珍珠', NULL, '2024-11-19 13:45:00', NULL, NULL, 0, '2024-11-19 13:40:00', '2025-11-19 18:18:32');
INSERT INTO `orders` VALUES (6, 'ORD202411190006', 3, '李四', 15.00, 0.00, 15.00, 15.00, 1, 1, '微信支付', NULL, '2025-11-26 15:46:42', NULL, NULL, 0, '2024-11-19 14:30:00', '2025-11-19 18:18:32');
INSERT INTO `orders` VALUES (7, 'ORD202411190007', 2, '张三', 28.00, 3.00, 25.00, 25.00, 1, 5, '用户取消', NULL, NULL, NULL, NULL, 0, '2024-11-19 15:10:00', '2025-11-19 18:18:32');
INSERT INTO `orders` VALUES (8, 'ORD202411190008', 3, '李四', 36.00, 6.00, 30.00, 30.00, 2, 4, '会员折扣', NULL, '2024-11-19 16:20:00', NULL, NULL, 0, '2024-11-19 16:15:00', '2025-11-19 18:18:32');
INSERT INTO `orders` VALUES (9, 'ORD202411150001', 7, 'VIP张三', 45.00, 5.00, 40.00, 40.00, 1, 4, NULL, NULL, '2024-11-15 10:30:00', '2024-11-15 11:00:00', NULL, 0, '2024-11-15 10:25:00', '2025-11-19 18:22:58');
INSERT INTO `orders` VALUES (10, 'ORD202411160001', 7, 'VIP张三', 38.00, 4.00, 34.00, 34.00, 1, 4, NULL, NULL, '2024-11-16 14:20:00', '2024-11-16 14:50:00', NULL, 0, '2024-11-16 14:15:00', '2025-11-19 18:22:58');
INSERT INTO `orders` VALUES (11, 'ORD202411170002', 7, 'VIP张三', 52.00, 6.00, 46.00, 46.00, 2, 4, NULL, NULL, '2024-11-17 16:10:00', '2024-11-17 16:40:00', NULL, 0, '2024-11-17 16:05:00', '2025-11-19 18:22:58');
INSERT INTO `orders` VALUES (12, 'ORD202411180003', 7, 'VIP张三', 42.00, 5.00, 37.00, 37.00, 1, 4, NULL, NULL, '2024-11-18 11:30:00', '2024-11-18 12:00:00', NULL, 0, '2024-11-18 11:25:00', '2025-11-19 18:22:58');
INSERT INTO `orders` VALUES (13, 'ORD202411140001', 8, 'VIP李四', 35.00, 3.00, 32.00, 32.00, 1, 4, NULL, NULL, '2024-11-14 15:00:00', '2024-11-14 15:30:00', NULL, 0, '2024-11-14 14:55:00', '2025-11-19 18:22:58');
INSERT INTO `orders` VALUES (14, 'ORD202411160002', 8, 'VIP李四', 48.00, 5.00, 43.00, 43.00, 1, 4, NULL, NULL, '2024-11-16 10:20:00', '2024-11-16 10:50:00', NULL, 0, '2024-11-16 10:15:00', '2025-11-19 18:22:58');
INSERT INTO `orders` VALUES (15, 'ORD202411180004', 8, 'VIP李四', 40.00, 4.00, 36.00, 36.00, 2, 4, NULL, NULL, '2024-11-18 16:40:00', '2024-11-18 17:10:00', NULL, 0, '2024-11-18 16:35:00', '2025-11-19 18:22:58');
INSERT INTO `orders` VALUES (16, 'ORD202411130001', 11, 'SVIP张总', 88.00, 10.00, 78.00, 78.00, 1, 4, NULL, NULL, '2024-11-13 12:00:00', '2024-11-13 12:30:00', NULL, 0, '2024-11-13 11:55:00', '2025-11-19 18:22:58');
INSERT INTO `orders` VALUES (17, 'ORD202411140002', 11, 'SVIP张总', 95.00, 12.00, 83.00, 83.00, 1, 4, NULL, NULL, '2024-11-14 14:30:00', '2024-11-14 15:00:00', NULL, 0, '2024-11-14 14:25:00', '2025-11-19 18:22:58');
INSERT INTO `orders` VALUES (18, 'ORD202411150002', 11, 'SVIP张总', 102.00, 15.00, 87.00, 87.00, 2, 4, NULL, NULL, '2024-11-15 16:00:00', '2024-11-15 16:30:00', NULL, 0, '2024-11-15 15:55:00', '2025-11-19 18:22:58');
INSERT INTO `orders` VALUES (19, 'ORD202411160003', 11, 'SVIP张总', 78.00, 8.00, 70.00, 70.00, 1, 4, NULL, NULL, '2024-11-16 11:20:00', '2024-11-16 11:50:00', NULL, 0, '2024-11-16 11:15:00', '2025-11-19 18:22:58');
INSERT INTO `orders` VALUES (20, 'ORD202411170003', 11, 'SVIP张总', 92.00, 10.00, 82.00, 82.00, 1, 4, NULL, NULL, '2024-11-17 13:40:00', '2024-11-17 14:10:00', NULL, 0, '2024-11-17 13:35:00', '2025-11-19 18:22:58');
INSERT INTO `orders` VALUES (21, 'ORD202411180005', 11, 'SVIP张总', 85.00, 9.00, 76.00, 76.00, 2, 4, NULL, NULL, '2024-11-18 15:20:00', '2024-11-18 15:50:00', NULL, 0, '2024-11-18 15:15:00', '2025-11-19 18:22:58');
INSERT INTO `orders` VALUES (22, 'ORD202411170004', 3, '王五', 28.00, 2.00, 26.00, 26.00, 1, 4, NULL, NULL, '2024-11-17 10:30:00', '2024-11-17 11:00:00', NULL, 0, '2024-11-17 10:25:00', '2025-11-19 18:22:58');
INSERT INTO `orders` VALUES (23, 'ORD202411180006', 4, '赵六', 32.00, 0.00, 32.00, 32.00, 1, 4, NULL, NULL, '2024-11-18 14:20:00', '2024-11-18 14:50:00', NULL, 0, '2024-11-18 14:15:00', '2025-11-19 18:22:58');
INSERT INTO `orders` VALUES (24, 'ORD202411190009', 5, '孙七', 25.00, 0.00, 25.00, 25.00, 1, 4, NULL, NULL, '2024-11-19 09:30:00', '2024-11-19 10:00:00', NULL, 0, '2024-11-19 09:25:00', '2025-11-19 18:22:58');
INSERT INTO `orders` VALUES (25, 'MT20251120122351553', 2, 'user1', 18.00, 0.00, 18.00, 18.00, 3, 1, '', NULL, '2025-11-26 15:46:42', NULL, NULL, 0, '2025-11-20 12:23:51', '2025-11-20 12:23:51');
INSERT INTO `orders` VALUES (26, 'MT20251120122353120', 2, 'user1', 18.00, 0.00, 18.00, 18.00, 3, 1, '', NULL, '2025-11-26 15:46:42', NULL, NULL, 0, '2025-11-20 12:23:54', '2025-11-20 12:23:54');
INSERT INTO `orders` VALUES (27, 'MT20251120122359985', 2, 'user1', 18.00, 0.00, 18.00, 18.00, 3, 1, '', NULL, '2025-11-26 15:46:41', NULL, NULL, 0, '2025-11-20 12:23:59', '2025-11-20 12:23:59');
INSERT INTO `orders` VALUES (28, 'MT20251120122400083', 2, 'user1', 18.00, 0.00, 18.00, 18.00, 3, 1, '', NULL, '2025-11-26 15:46:40', NULL, NULL, 0, '2025-11-20 12:24:00', '2025-11-20 12:24:00');
INSERT INTO `orders` VALUES (29, 'MT20251120122401086', 2, 'user1', 18.00, 0.00, 18.00, 18.00, 3, 2, '', NULL, '2025-11-26 15:46:40', NULL, NULL, 0, '2025-11-20 12:24:01', '2025-11-20 12:24:01');
INSERT INTO `orders` VALUES (30, 'MT20251120122401653', 2, 'user1', 18.00, 0.00, 18.00, 18.00, 3, 3, '', NULL, '2025-11-26 15:46:40', NULL, NULL, 0, '2025-11-20 12:24:02', '2025-11-20 12:24:02');
INSERT INTO `orders` VALUES (31, 'MT20251120122515732', 2, 'user1', 13.00, 0.00, 13.00, 13.00, 3, 3, '', NULL, '2025-11-26 15:46:39', NULL, NULL, 0, '2025-11-20 12:25:16', '2025-11-20 12:25:16');
INSERT INTO `orders` VALUES (32, 'MT20251120122519088', 2, 'user1', 13.00, 0.00, 13.00, 13.00, 3, 3, '', NULL, '2025-11-26 15:46:39', NULL, NULL, 0, '2025-11-20 12:25:20', '2025-11-20 12:25:20');
INSERT INTO `orders` VALUES (33, 'MT20251120122522883', 2, 'user1', 13.00, 0.00, 13.00, 13.00, 1, 3, '', NULL, '2025-11-26 15:46:38', NULL, NULL, 0, '2025-11-20 12:25:22', '2025-11-20 12:25:22');
INSERT INTO `orders` VALUES (34, 'MT20251120122830645', 2, 'user1', 13.00, 0.00, 13.00, 13.00, 1, 3, '', NULL, '2025-11-26 15:46:38', NULL, NULL, 0, '2025-11-20 12:28:31', '2025-11-20 12:28:31');
INSERT INTO `orders` VALUES (35, 'MT20251120125440947', 2, 'user1', 15.00, 0.00, 15.00, 15.00, 1, 2, '', NULL, '2025-11-26 15:46:38', NULL, NULL, 0, '2025-11-20 12:54:40', '2025-11-20 12:54:40');
INSERT INTO `orders` VALUES (36, 'MT20251120125512713', 2, 'user1', 36.00, 0.00, 36.00, 36.00, 3, 3, '', NULL, '2025-11-26 15:46:37', NULL, NULL, 0, '2025-11-20 12:55:12', '2025-11-20 12:55:12');
INSERT INTO `orders` VALUES (37, 'MT20251120125522306', 2, 'user1', 36.00, 0.00, 36.00, 36.00, 3, 3, '', NULL, '2025-11-26 15:46:37', NULL, NULL, 0, '2025-11-20 12:55:23', '2025-11-20 12:55:23');
INSERT INTO `orders` VALUES (38, 'MT20251120125538612', 2, 'user1', 36.00, 0.00, 36.00, 36.00, 3, 3, '', NULL, '2025-11-26 15:46:36', NULL, NULL, 0, '2025-11-20 12:55:38', '2025-11-20 12:55:38');
INSERT INTO `orders` VALUES (39, 'MT20251120125616769', 2, 'user1', 36.00, 0.00, 36.00, 36.00, 3, 3, '', NULL, '2025-11-26 15:46:36', NULL, NULL, 0, '2025-11-20 12:56:16', '2025-11-20 12:56:16');
INSERT INTO `orders` VALUES (40, 'MT20251120125929455', 2, 'user1', 36.00, 0.00, 36.00, 36.00, 3, 4, '', NULL, '2025-11-26 15:46:36', '2025-11-26 15:47:10', NULL, 0, '2025-11-20 12:59:29', '2025-11-20 12:59:29');
INSERT INTO `orders` VALUES (41, 'MT20251120130241047', 2, 'user1', 36.00, 0.00, 36.00, 36.00, 3, 4, '', NULL, '2025-11-26 15:46:35', '2025-11-26 15:47:10', NULL, 0, '2025-11-20 13:02:41', '2025-11-20 13:02:41');
INSERT INTO `orders` VALUES (42, 'MT20251120141012666', 2, 'user1', 12.00, 0.00, 12.00, 12.00, 3, 4, '', NULL, '2025-11-26 15:46:35', '2025-11-26 15:47:10', NULL, 0, '2025-11-20 14:10:12', '2025-11-20 14:10:12');
INSERT INTO `orders` VALUES (43, 'MT20251120142818432', 2, 'user1', 12.00, 0.00, 12.00, 12.00, 3, 4, '', NULL, '2025-11-20 14:29:51', '2025-11-26 11:05:08', NULL, 0, '2025-11-20 14:28:18', '2025-11-20 14:28:18');
INSERT INTO `orders` VALUES (44, 'MT20251120143349032', 2, 'user1', 13.00, 0.00, 13.00, 13.00, 3, 4, '', NULL, '2025-11-26 15:46:34', '2025-11-26 15:47:09', NULL, 0, '2025-11-20 14:33:50', '2025-11-20 14:33:50');
INSERT INTO `orders` VALUES (45, 'MT20251120143613461', 2, 'user1', 12.00, 0.00, 12.00, 12.00, 3, 4, '', NULL, '2025-11-26 15:46:34', '2025-11-26 15:47:08', NULL, 0, '2025-11-20 14:36:14', '2025-11-20 14:36:14');
INSERT INTO `orders` VALUES (46, 'MT20251120143907803', 21, '15045678901', 16.00, 0.00, 16.00, 16.00, 3, 5, '', NULL, NULL, NULL, '2025-11-20 15:15:52', 0, '2025-11-20 14:39:08', '2025-11-20 14:39:08');
INSERT INTO `orders` VALUES (47, 'MT20251120143926164', 21, '15045678901', 16.00, 0.00, 16.00, 16.00, 3, 5, '', NULL, NULL, NULL, '2025-11-20 15:15:49', 0, '2025-11-20 14:39:26', '2025-11-20 14:39:26');
INSERT INTO `orders` VALUES (48, 'MT20251120143948987', 21, '15045678901', 16.00, 0.00, 16.00, 16.00, 3, 5, '', NULL, NULL, NULL, '2025-11-20 15:15:46', 0, '2025-11-20 14:39:48', '2025-11-20 14:39:48');
INSERT INTO `orders` VALUES (49, 'MT20251120143951081', 21, '15045678901', 16.00, 0.00, 16.00, 16.00, 3, 5, '', NULL, NULL, NULL, '2025-11-20 15:15:42', 0, '2025-11-20 14:39:52', '2025-11-20 14:39:52');
INSERT INTO `orders` VALUES (50, 'MT20251120143955421', 21, '15045678901', 16.00, 0.00, 16.00, 16.00, 2, 5, '', NULL, NULL, NULL, '2025-11-20 15:15:45', 0, '2025-11-20 14:39:55', '2025-11-20 14:39:55');
INSERT INTO `orders` VALUES (51, 'MT20251120143957100', 21, '15045678901', 16.00, 0.00, 16.00, 16.00, 1, 5, '', NULL, NULL, NULL, '2025-11-20 15:16:05', 0, '2025-11-20 14:39:58', '2025-11-20 14:39:58');
INSERT INTO `orders` VALUES (52, 'MT20251120144225234', 21, '15045678901', 15.00, 0.00, 15.00, 15.00, 3, 5, '', NULL, NULL, NULL, '2025-11-20 15:15:40', 0, '2025-11-20 14:42:26', '2025-11-20 14:42:26');
INSERT INTO `orders` VALUES (53, 'MT20251120144341203', 21, '15045678901', 15.00, 0.00, 15.00, 15.00, 3, 5, '', NULL, NULL, NULL, '2025-11-20 15:15:37', 0, '2025-11-20 14:43:42', '2025-11-20 14:43:42');
INSERT INTO `orders` VALUES (54, 'MT20251120144553373', 21, '15045678901', 15.00, 0.00, 15.00, 15.00, 3, 5, '', NULL, NULL, NULL, '2025-11-20 15:15:31', 0, '2025-11-20 14:45:54', '2025-11-20 14:45:54');
INSERT INTO `orders` VALUES (55, 'MT20251120144928607', 21, '15045678901', 15.00, 0.00, 15.00, 15.00, 3, 4, '', NULL, '2025-11-20 14:49:29', '2025-11-20 15:25:15', NULL, 0, '2025-11-20 14:49:29', '2025-11-20 14:49:29');
INSERT INTO `orders` VALUES (56, 'MT20251126110140263', 21, '15045678901', 27.00, 0.00, 27.00, 27.00, 3, 4, '', NULL, '2025-11-26 11:01:40', '2025-11-26 11:05:07', NULL, 0, '2025-11-26 11:01:40', '2025-11-26 11:01:40');
INSERT INTO `orders` VALUES (57, 'MT20251126152919901', 21, '15045678901', 13.00, 0.00, 13.00, 13.00, 1, 4, '', NULL, '2025-11-26 15:29:19', '2025-11-26 15:47:07', NULL, 0, '2025-11-26 15:29:19', '2025-11-26 15:29:19');

-- ----------------------------
-- Table structure for points_history
-- ----------------------------
DROP TABLE IF EXISTS `points_history`;
CREATE TABLE `points_history`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `points` int(11) NOT NULL COMMENT '积分变动（正数为增加，负数为减少）',
  `type` int(11) NOT NULL COMMENT '类型：1-购物，2-签到，3-邀请，4-评价，5-生日，6-兑换，7-过期',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT '关联订单ID',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '积分历史表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_history
-- ----------------------------
INSERT INTO `points_history` VALUES (1, 2, 100, 1, NULL, '购物消费获得积分', '2024-11-20 10:30:00');
INSERT INTO `points_history` VALUES (2, 2, 10, 2, NULL, '每日签到', '2024-11-21 08:00:00');
INSERT INTO `points_history` VALUES (3, 2, 50, 3, NULL, '邀请好友注册', '2024-11-22 14:20:00');
INSERT INTO `points_history` VALUES (4, 2, 10, 2, NULL, '每日签到', '2024-11-22 08:00:00');
INSERT INTO `points_history` VALUES (5, 2, 5, 4, NULL, '订单评价', '2024-11-23 16:30:00');

-- ----------------------------
-- Table structure for points_product
-- ----------------------------
DROP TABLE IF EXISTS `points_product`;
CREATE TABLE `points_product`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商品描述',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商品图片',
  `points` int(11) NOT NULL COMMENT '所需积分',
  `stock` int(11) NOT NULL DEFAULT 0 COMMENT '库存数量',
  `exchanged` int(11) NOT NULL DEFAULT 0 COMMENT '已兑换数量',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `deleted` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_sort`(`sort` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '积分商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_product
-- ----------------------------
INSERT INTO `points_product` VALUES (1, '珍珠奶茶券', '可兑换任意一杯珍珠奶茶', 'https://picsum.photos/300/300?random=21', 100, 1000, 0, 1, 1, 0, '2025-11-26 10:39:17', '2025-11-26 10:50:53');
INSERT INTO `points_product` VALUES (2, '芝士奶盖券', '可兑换任意一杯芝士奶盖', 'https://picsum.photos/300/300?random=22', 150, 800, 0, 1, 2, 0, '2025-11-26 10:39:17', '2025-11-26 10:50:53');
INSERT INTO `points_product` VALUES (3, '5元优惠券', '全场通用5元优惠券', 'https://picsum.photos/300/300?random=23', 50, 2000, 0, 1, 3, 0, '2025-11-26 10:39:17', '2025-11-26 10:50:53');
INSERT INTO `points_product` VALUES (4, '10元优惠券', '全场通用10元优惠券', 'https://picsum.photos/300/300?random=24', 100, 1500, 0, 1, 4, 0, '2025-11-26 10:39:17', '2025-11-26 10:50:53');
INSERT INTO `points_product` VALUES (5, '奶茶杯套', '精美奶茶杯套一个', 'https://picsum.photos/300/300?random=25', 200, 500, 0, 1, 5, 0, '2025-11-26 10:39:17', '2025-11-26 10:50:53');
INSERT INTO `points_product` VALUES (6, '品牌帆布袋', '品牌定制帆布袋', 'https://picsum.photos/300/300?random=26', 300, 300, 0, 1, 6, 0, '2025-11-26 10:39:17', '2025-11-26 10:50:53');
INSERT INTO `points_product` VALUES (7, '珍珠奶茶券', '可兑换任意一杯珍珠奶茶', '/static/points/product1.jpg', 100, 1000, 0, 1, 1, 0, '2025-11-26 10:39:56', '2025-11-26 10:39:56');
INSERT INTO `points_product` VALUES (8, '芝士奶盖券', '可兑换任意一杯芝士奶盖', '/static/points/product2.jpg', 150, 800, 0, 1, 2, 0, '2025-11-26 10:39:56', '2025-11-26 10:39:56');
INSERT INTO `points_product` VALUES (9, '5元优惠券', '全场通用5元优惠券', '/static/points/product3.jpg', 50, 2000, 0, 1, 3, 0, '2025-11-26 10:39:56', '2025-11-26 10:39:56');
INSERT INTO `points_product` VALUES (10, '10元优惠券', '全场通用10元优惠券', '/static/points/product4.jpg', 100, 1500, 0, 1, 4, 0, '2025-11-26 10:39:56', '2025-11-26 10:39:56');
INSERT INTO `points_product` VALUES (11, '奶茶杯套', '精美奶茶杯套一个', '/static/points/product5.jpg', 200, 500, 0, 1, 5, 0, '2025-11-26 10:39:56', '2025-11-26 10:39:56');
INSERT INTO `points_product` VALUES (12, '品牌帆布袋', '品牌定制帆布袋', '/static/points/product6.jpg', 300, 300, 0, 1, 6, 0, '2025-11-26 10:39:56', '2025-11-26 10:39:56');
INSERT INTO `points_product` VALUES (13, '珍珠奶茶券', '可兑换任意一杯珍珠奶茶', '/static/points/product1.jpg', 100, 1000, 0, 1, 1, 0, '2025-11-26 10:41:35', '2025-11-26 10:41:35');
INSERT INTO `points_product` VALUES (14, '芝士奶盖券', '可兑换任意一杯芝士奶盖', '/static/points/product2.jpg', 150, 800, 0, 1, 2, 0, '2025-11-26 10:41:35', '2025-11-26 10:41:35');
INSERT INTO `points_product` VALUES (15, '5元优惠券', '全场通用5元优惠券', '/static/points/product3.jpg', 50, 2000, 0, 1, 3, 0, '2025-11-26 10:41:35', '2025-11-26 10:41:35');
INSERT INTO `points_product` VALUES (16, '10元优惠券', '全场通用10元优惠券', '/static/points/product4.jpg', 100, 1500, 0, 1, 4, 0, '2025-11-26 10:41:35', '2025-11-26 10:41:35');
INSERT INTO `points_product` VALUES (17, '奶茶杯套', '精美奶茶杯套一个', '/static/points/product5.jpg', 200, 500, 0, 1, 5, 0, '2025-11-26 10:41:35', '2025-11-26 10:41:35');
INSERT INTO `points_product` VALUES (18, '品牌帆布袋', '品牌定制帆布袋', '/static/points/product6.jpg', 300, 300, 0, 1, 6, 0, '2025-11-26 10:41:35', '2025-11-26 10:41:35');

-- ----------------------------
-- Table structure for print_device
-- ----------------------------
DROP TABLE IF EXISTS `print_device`;
CREATE TABLE `print_device`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '设备ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设备名称',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设备类型：thermal-热敏，dot-针式，laser-激光',
  `connection` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '连接方式：network-网络，usb-USB，bluetooth-蓝牙',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `port` int(11) NULL DEFAULT NULL COMMENT '端口',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'USB设备路径',
  `mac` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '蓝牙MAC地址',
  `status` int(11) NULL DEFAULT 1 COMMENT '状态：0-离线，1-在线',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `deleted` int(11) NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '打印设备表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of print_device
-- ----------------------------
INSERT INTO `print_device` VALUES (1, '前台打印机', 'thermal', 'network', '192.168.1.100', 9100, NULL, NULL, 1, '前台收银打印机，用于打印订单小票', 0, '2025-11-20 08:32:42', '2025-11-20 08:32:42');
INSERT INTO `print_device` VALUES (2, '厨房打印机', 'thermal', 'network', '192.168.1.101', 9100, NULL, NULL, 1, '厨房订单打印机，用于打印制作单', 0, '2025-11-20 08:32:42', '2025-11-20 08:32:42');
INSERT INTO `print_device` VALUES (3, '备用打印机', 'thermal', 'network', '192.168.1.102', 9100, NULL, NULL, 1, '备用打印机，离线状态', 0, '2025-11-20 08:32:42', '2025-11-20 08:32:42');

-- ----------------------------
-- Table structure for print_record
-- ----------------------------
DROP TABLE IF EXISTS `print_record`;
CREATE TABLE `print_record`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `device_id` bigint(20) NOT NULL COMMENT '设备ID',
  `template_id` bigint(20) NOT NULL COMMENT '模板ID',
  `status` int(11) NULL DEFAULT 1 COMMENT '状态：1-成功，2-失败，3-处理中',
  `error_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '错误信息',
  `copies` int(11) NULL DEFAULT 1 COMMENT '打印份数',
  `duration` int(11) NULL DEFAULT NULL COMMENT '打印耗时(秒)',
  `retry_count` int(11) NULL DEFAULT 0 COMMENT '重试次数',
  `print_time` datetime NULL DEFAULT NULL COMMENT '打印时间',
  `deleted` int(11) NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_device_id`(`device_id` ASC) USING BTREE,
  INDEX `idx_template_id`(`template_id` ASC) USING BTREE,
  CONSTRAINT `fk_print_record_device` FOREIGN KEY (`device_id`) REFERENCES `print_device` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_print_record_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_print_record_template` FOREIGN KEY (`template_id`) REFERENCES `print_template` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '打印记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of print_record
-- ----------------------------
INSERT INTO `print_record` VALUES (1, 1, 1, 1, 1, NULL, 1, 2, 0, '2024-11-19 09:26:00', 0, '2025-11-20 08:32:42', '2025-11-20 08:32:42');
INSERT INTO `print_record` VALUES (2, 2, 2, 2, 1, NULL, 1, 2, 0, '2024-11-19 10:11:00', 0, '2025-11-20 08:32:42', '2025-11-20 08:32:42');
INSERT INTO `print_record` VALUES (3, 3, 1, 1, 1, NULL, 1, 2, 0, '2024-11-19 11:16:00', 0, '2025-11-20 08:32:42', '2025-11-20 08:32:42');
INSERT INTO `print_record` VALUES (4, 4, 3, 1, 2, '设备离线，无法连接', 1, 0, 1, '2024-11-19 12:01:00', 0, '2025-11-20 08:32:42', '2025-11-20 08:32:42');
INSERT INTO `print_record` VALUES (5, 8, 1, 1, 1, NULL, 1, 2, 0, '2025-11-20 08:33:18', 0, '2025-11-20 08:33:18', '2025-11-20 08:33:18');
INSERT INTO `print_record` VALUES (6, 8, 1, 1, 1, NULL, 1, 2, 0, '2025-11-20 08:33:40', 0, '2025-11-20 08:33:40', '2025-11-20 08:33:40');
INSERT INTO `print_record` VALUES (7, 4, 3, 1, 1, NULL, 1, 2, 0, '2025-11-20 08:37:47', 0, '2025-11-20 08:37:47', '2025-11-20 08:37:47');

-- ----------------------------
-- Table structure for print_template
-- ----------------------------
DROP TABLE IF EXISTS `print_template`;
CREATE TABLE `print_template`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板名称',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板类型：order-订单小票，kitchen-厨房单，delivery-配送单',
  `width` int(11) NOT NULL COMMENT '纸张宽度(mm)',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板内容',
  `header` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '页眉',
  `footer` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '页脚',
  `is_default` int(11) NULL DEFAULT 0 COMMENT '是否默认：0-否，1-是',
  `deleted` int(11) NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '打印模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of print_template
-- ----------------------------
INSERT INTO `print_template` VALUES (1, '标准订单小票', 'order', 80, '订单号: {orderNo}\r\n客户: {customerName}\r\n--------------------------------\r\n商品清单:\r\n{items}\r\n--------------------------------\r\n订单金额: {totalAmount}\r\n下单时间: {createTime}', '========== 奶茶小店 ==========', '谢谢惠顾，欢迎再次光临！\r\n客服电话: 400-123-4567\r\n==============================', 1, 0, '2025-11-20 08:32:42', '2025-11-20 08:32:42');
INSERT INTO `print_template` VALUES (2, '厨房制作单', 'kitchen', 80, '【制作单】\r\n订单号: {orderNo}\r\n--------------------------------\r\n制作清单:\r\n{items}\r\n--------------------------------\r\n下单时间: {createTime}\r\n备注: 请按顺序制作', '========== 厨房制作单 ==========', '==============================', 1, 0, '2025-11-20 08:32:42', '2025-11-20 08:32:42');
INSERT INTO `print_template` VALUES (3, '配送单', 'delivery', 80, '【配送单】\r\n订单号: {orderNo}\r\n客户: {customerName}\r\n--------------------------------\r\n配送商品:\r\n{items}\r\n--------------------------------\r\n配送地址: 待补充\r\n联系电话: 待补充\r\n下单时间: {createTime}', '========== 配送单 ==========', '请核对商品后配送\r\n==============================', 1, 0, '2025-11-20 08:32:42', '2025-11-20 08:32:42');

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品描述',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品图片',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `member_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '会员价格',
  `cost` decimal(10, 2) NULL DEFAULT NULL COMMENT '成本价格',
  `stock` int(11) NULL DEFAULT 0 COMMENT '库存',
  `sales` int(11) NULL DEFAULT 0 COMMENT '销量',
  `nutrition` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '营养成分',
  `status` int(11) NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
  `is_recommend` int(11) NULL DEFAULT 0 COMMENT '是否推荐：0-否，1-是',
  `sort` int(11) NULL DEFAULT 0 COMMENT '排序',
  `deleted` int(11) NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 48 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1, '珍珠奶茶', 1, '经典珍珠奶茶，香浓可口', 'https://picsum.photos/400/400?random=1', 12.00, 10.80, 8.00, 78, 156, '热量：200kcal', 1, 1, 1, 0, '2025-11-19 18:18:32', '2025-11-26 10:50:52');
INSERT INTO `product` VALUES (2, '波霸奶茶', 1, '大颗粒珍珠，Q弹有嚼劲', 'https://picsum.photos/400/400?random=2', 13.00, 11.70, 8.50, 94, 98, '热量：210kcal', 1, 1, 2, 0, '2025-11-19 18:18:32', '2025-11-26 10:50:52');
INSERT INTO `product` VALUES (3, '红豆奶茶', 1, '香甜红豆配奶茶', 'https://picsum.photos/400/400?random=3', 14.00, 12.60, 9.00, 100, 78, '热量：220kcal', 1, 0, 3, 0, '2025-11-19 18:18:32', '2025-11-26 10:50:52');
INSERT INTO `product` VALUES (4, '芋泥奶茶', 1, '浓郁芋泥，香甜可口', 'https://picsum.photos/400/400?random=4', 15.00, 13.50, 10.00, 97, 145, '热量：230kcal', 1, 1, 4, 0, '2025-11-19 18:18:32', '2025-11-26 10:50:52');
INSERT INTO `product` VALUES (5, '百香果茶', 2, '清新百香果，酸甜可口', 'https://picsum.photos/400/400?random=5', 16.00, 14.40, 10.50, 100, 112, '热量：150kcal', 1, 1, 5, 0, '2025-11-19 18:18:32', '2025-11-26 10:50:52');
INSERT INTO `product` VALUES (6, '柠檬茶', 2, '新鲜柠檬，清爽解渴', 'https://picsum.photos/400/400?random=6', 14.00, 12.60, 9.00, 100, 89, '热量：120kcal', 1, 0, 6, 0, '2025-11-19 18:18:32', '2025-11-26 10:50:53');
INSERT INTO `product` VALUES (7, '芝士奶盖茶', 2, '浓郁芝士奶盖', 'https://picsum.photos/400/400?random=7', 18.00, 16.20, 12.00, 94, 134, '热量：280kcal', 1, 1, 7, 0, '2025-11-19 18:18:32', '2025-11-26 10:50:53');
INSERT INTO `product` VALUES (8, '美式咖啡', 2, '经典美式咖啡', 'https://picsum.photos/400/400?random=8', 15.00, 13.50, 10.00, 100, 67, '热量：5kcal', 1, 0, 8, 0, '2025-11-19 18:18:32', '2025-11-26 10:50:53');
INSERT INTO `product` VALUES (9, '拿铁咖啡', 2, '香浓拿铁', 'https://picsum.photos/400/400?random=9', 18.00, 16.20, 12.00, 100, 92, '热量：180kcal', 1, 0, 9, 0, '2025-11-19 18:18:32', '2025-11-26 10:50:53');
INSERT INTO `product` VALUES (10, '芝士蛋糕', 3, '香滑芝士蛋糕', 'https://picsum.photos/400/400?random=10', 25.00, 22.50, 15.00, 0, 56, '热量：350kcal', 0, 1, 10, 0, '2025-11-19 18:18:32', '2025-11-26 10:50:53');
INSERT INTO `product` VALUES (11, '布蕾奶茶', 4, '焦糖布蕾风味，香甜丝滑', 'https://picsum.photos/400/400?random=11', 16.00, 14.40, 10.50, 100, 88, '热量：420kcal', 1, 1, 11, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (12, '仙草奶茶', 4, '清凉仙草，解暑佳品', 'https://picsum.photos/400/400?random=12', 13.00, 11.70, 8.50, 100, 95, '热量：380kcal', 1, 0, 12, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (13, '双拼奶茶', 4, '珍珠+椰果双重口感', 'https://picsum.photos/400/400?random=13', 15.00, 13.50, 9.50, 100, 102, '热量：430kcal', 1, 1, 13, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (14, '焦糖奶茶', 4, '浓郁焦糖香，甜而不腻', 'https://picsum.photos/400/400?random=14', 14.00, 12.60, 9.00, 100, 76, '热量：410kcal', 1, 0, 14, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (15, '巧克力奶茶', 4, '丝滑巧克力，醇厚浓香', 'https://picsum.photos/400/400?random=15', 16.00, 14.40, 10.50, 100, 84, '热量：450kcal', 1, 1, 15, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (16, '椰果奶茶', 4, 'Q弹椰果，清爽可口', 'https://picsum.photos/400/400?random=16', 13.00, 11.70, 8.50, 100, 91, '热量：390kcal', 1, 0, 16, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (17, '抹茶奶茶', 4, '日式抹茶，清新淡雅', 'https://picsum.photos/400/400?random=17', 15.00, 13.50, 10.00, 100, 87, '热量：400kcal', 1, 1, 17, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (18, '紫薯奶茶', 4, '香甜紫薯，营养健康', 'https://picsum.photos/400/400?random=18', 15.00, 13.50, 10.00, 100, 69, '热量：420kcal', 1, 0, 18, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (19, '草莓果茶', 5, '新鲜草莓，酸甜可口', 'https://picsum.photos/400/400?random=19', 18.00, 16.20, 12.00, 100, 125, '热量：180kcal', 1, 1, 21, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (20, '芒果果茶', 5, '香甜芒果，热带风情', 'https://picsum.photos/400/400?random=20', 18.00, 16.20, 12.00, 100, 118, '热量：190kcal', 1, 1, 22, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (21, '西瓜果茶', 5, '清爽西瓜，夏日必备', 'https://picsum.photos/400/400?random=21', 16.00, 14.40, 10.50, 100, 96, '热量：160kcal', 1, 0, 23, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (22, '蜜桃果茶', 5, '香甜蜜桃，果香浓郁', 'https://picsum.photos/400/400?random=22', 17.00, 15.30, 11.00, 100, 108, '热量：170kcal', 1, 1, 24, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (23, '葡萄柚果茶', 5, '清新葡萄柚，酸甜平衡', 'https://picsum.photos/400/400?random=23', 17.00, 15.30, 11.00, 100, 89, '热量：150kcal', 1, 0, 25, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (24, '荔枝果茶', 5, '香甜荔枝，清爽怡人', 'https://picsum.photos/400/400?random=24', 18.00, 16.20, 12.00, 100, 94, '热量：180kcal', 1, 1, 26, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (25, '橙子果茶', 5, '新鲜橙子，维C满满', 'https://picsum.photos/400/400?random=25', 15.00, 13.50, 10.00, 100, 102, '热量：140kcal', 1, 0, 27, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (26, '奇异果茶', 5, '酸甜奇异果，营养丰富', 'https://picsum.photos/400/400?random=26', 17.00, 15.30, 11.00, 100, 78, '热量：160kcal', 1, 0, 28, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (27, '草莓奶盖', 6, '草莓果茶配芝士奶盖', 'https://picsum.photos/400/400?random=27', 20.00, 18.00, 13.00, 100, 115, '热量：380kcal', 1, 1, 31, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (28, '芒果奶盖', 6, '芒果果茶配芝士奶盖', 'https://picsum.photos/400/400?random=28', 20.00, 18.00, 13.00, 100, 108, '热量：390kcal', 1, 1, 32, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (29, '乌龙奶盖', 6, '乌龙茶配芝士奶盖', 'https://picsum.photos/400/400?random=29', 19.00, 17.10, 12.50, 100, 97, '热量：360kcal', 1, 0, 33, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (30, '茉莉奶盖', 6, '茉莉绿茶配芝士奶盖', 'https://picsum.photos/400/400?random=30', 19.00, 17.10, 12.50, 100, 89, '热量：350kcal', 1, 0, 34, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (31, '红茶奶盖', 6, '红茶配芝士奶盖', 'https://picsum.photos/400/400?random=31', 18.00, 16.20, 12.00, 100, 112, '热量：340kcal', 1, 1, 35, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (32, '抹茶奶盖', 6, '抹茶配芝士奶盖', 'https://picsum.photos/400/400?random=32', 20.00, 18.00, 13.00, 100, 95, '热量：370kcal', 1, 1, 36, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (33, '蜜桃奶盖', 6, '蜜桃果茶配芝士奶盖', 'https://picsum.photos/400/400?random=33', 20.00, 18.00, 13.00, 100, 86, '热量：380kcal', 1, 0, 37, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (34, '卡布奇诺', 2, '经典意式咖啡', 'https://picsum.photos/400/400?random=34', 18.00, 16.20, 12.00, 100, 78, '热量：180kcal', 1, 0, 41, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (35, '摩卡咖啡', 2, '巧克力风味咖啡', 'https://picsum.photos/400/400?random=35', 20.00, 18.00, 13.00, 100, 85, '热量：220kcal', 1, 1, 42, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (36, '焦糖玛奇朵', 2, '焦糖风味拿铁', 'https://picsum.photos/400/400?random=36', 22.00, 19.80, 14.00, 100, 92, '热量：250kcal', 1, 1, 43, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (37, '香草拿铁', 2, '香草风味拿铁', 'https://picsum.photos/400/400?random=37', 19.00, 17.10, 12.50, 100, 73, '热量：200kcal', 1, 0, 44, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (38, '冰美式', 2, '冰镇美式咖啡', 'https://picsum.photos/400/400?random=38', 15.00, 13.50, 10.00, 100, 88, '热量：10kcal', 1, 0, 45, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (39, '冰拿铁', 2, '冰镇拿铁咖啡', 'https://picsum.photos/400/400?random=39', 18.00, 16.20, 12.00, 100, 95, '热量：180kcal', 1, 1, 46, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (40, '提拉米苏', 3, '经典意式甜点', 'https://picsum.photos/400/400?random=40', 28.00, 25.20, 18.00, 50, 45, '热量：380kcal', 1, 1, 51, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (41, '布朗尼', 3, '浓郁巧克力蛋糕', 'https://picsum.photos/400/400?random=41', 22.00, 19.80, 14.00, 50, 52, '热量：320kcal', 1, 0, 52, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (42, '抹茶蛋糕', 3, '清新抹茶风味', 'https://picsum.photos/400/400?random=42', 25.00, 22.50, 16.00, 50, 38, '热量：300kcal', 1, 1, 53, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (43, '草莓蛋糕', 3, '新鲜草莓蛋糕', 'https://picsum.photos/400/400?random=43', 26.00, 23.40, 17.00, 50, 42, '热量：310kcal', 1, 0, 54, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (44, '曲奇饼干', 3, '香脆曲奇', 'https://picsum.photos/400/400?random=44', 15.00, 13.50, 9.00, 100, 67, '热量：180kcal', 1, 0, 55, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (45, '马卡龙', 3, '法式马卡龙', 'https://picsum.photos/400/400?random=45', 18.00, 16.20, 11.00, 80, 58, '热量：150kcal', 1, 1, 56, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (46, '泡芙', 3, '奶油泡芙', 'https://picsum.photos/400/400?random=46', 12.00, 10.80, 7.00, 80, 71, '热量：200kcal', 1, 0, 57, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');
INSERT INTO `product` VALUES (47, '蛋挞', 3, '葡式蛋挞', 'https://picsum.photos/400/400?random=47', 8.00, 7.20, 4.50, 1, 89, '热量：220kcal', 1, 1, 58, 0, '2025-11-26 15:03:01', '2025-11-26 15:03:01');

-- ----------------------------
-- Table structure for product_collection
-- ----------------------------
DROP TABLE IF EXISTS `product_collection`;
CREATE TABLE `product_collection`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `deleted` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_product`(`user_id` ASC, `product_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_collection
-- ----------------------------

-- ----------------------------
-- Table structure for recipe
-- ----------------------------
DROP TABLE IF EXISTS `recipe`;
CREATE TABLE `recipe`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配方ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `ingredient_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '原料名称',
  `quantity` decimal(10, 2) NOT NULL COMMENT '用量',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '单位',
  `unit_cost` decimal(10, 2) NOT NULL COMMENT '单价',
  `total_cost` decimal(10, 2) NOT NULL COMMENT '总成本',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '说明',
  `sort` int(11) NULL DEFAULT 0 COMMENT '排序',
  `deleted` int(11) NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品配方表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recipe
-- ----------------------------
INSERT INTO `recipe` VALUES (1, 1, '茶叶', 5.00, 'g', 0.50, 2.50, '优质红茶', 1, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (2, 1, '牛奶', 200.00, 'ml', 0.01, 2.00, '新鲜牛奶', 2, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (3, 1, '珍珠', 30.00, 'g', 0.08, 2.40, 'Q弹珍珠', 3, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (4, 1, '糖浆', 20.00, 'ml', 0.05, 1.00, '蔗糖糖浆', 4, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (5, 2, '茶叶', 5.00, 'g', 0.50, 2.50, '优质红茶', 1, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (6, 2, '牛奶', 200.00, 'ml', 0.01, 2.00, '新鲜牛奶', 2, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (7, 2, '波霸', 40.00, 'g', 0.10, 4.00, '大颗粒波霸', 3, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (8, 2, '糖浆', 20.00, 'ml', 0.05, 1.00, '蔗糖糖浆', 4, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (9, 3, '茶叶', 5.00, 'g', 0.50, 2.50, '优质红茶', 1, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (10, 3, '牛奶', 200.00, 'ml', 0.01, 2.00, '新鲜牛奶', 2, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (11, 3, '红豆', 50.00, 'g', 0.06, 3.00, '蜜制红豆', 3, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (12, 3, '糖浆', 20.00, 'ml', 0.05, 1.00, '蔗糖糖浆', 4, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (13, 4, '茶叶', 5.00, 'g', 0.50, 2.50, '优质红茶', 1, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (14, 4, '牛奶', 200.00, 'ml', 0.01, 2.00, '新鲜牛奶', 2, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (15, 4, '芋泥', 60.00, 'g', 0.12, 7.20, '香浓芋泥', 3, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (16, 4, '糖浆', 15.00, 'ml', 0.05, 0.75, '蔗糖糖浆', 4, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (17, 5, '茶叶', 4.00, 'g', 0.50, 2.00, '优质绿茶', 1, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (18, 5, '百香果浆', 80.00, 'ml', 0.15, 12.00, '新鲜百香果浆', 2, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (19, 5, '蜂蜜', 25.00, 'ml', 0.08, 2.00, '天然蜂蜜', 3, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (20, 5, '柠檬汁', 10.00, 'ml', 0.10, 1.00, '新鲜柠檬汁', 4, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (21, 6, '茶叶', 4.00, 'g', 0.50, 2.00, '优质绿茶', 1, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (22, 6, '柠檬片', 3.00, '片', 0.50, 1.50, '新鲜柠檬片', 2, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (23, 6, '蜂蜜', 20.00, 'ml', 0.08, 1.60, '天然蜂蜜', 3, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (24, 6, '薄荷叶', 5.00, '片', 0.20, 1.00, '新鲜薄荷叶', 4, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (25, 7, '茶叶', 5.00, 'g', 0.50, 2.50, '优质乌龙茶', 1, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (26, 7, '芝士粉', 30.00, 'g', 0.20, 6.00, '进口芝士粉', 2, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (27, 7, '淡奶油', 100.00, 'ml', 0.03, 3.00, '动物性淡奶油', 3, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (28, 7, '糖浆', 15.00, 'ml', 0.05, 0.75, '蔗糖糖浆', 4, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (29, 8, '咖啡豆', 20.00, 'g', 0.80, 16.00, '优质阿拉比卡咖啡豆', 1, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (30, 8, '热水', 300.00, 'ml', 0.00, 0.30, '纯净水', 2, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (31, 9, '咖啡豆', 18.00, 'g', 0.80, 14.40, '优质阿拉比卡咖啡豆', 1, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (32, 9, '牛奶', 250.00, 'ml', 0.01, 2.50, '新鲜全脂牛奶', 2, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (33, 9, '糖浆', 15.00, 'ml', 0.05, 0.75, '香草糖浆', 3, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (34, 9, '奶泡', 50.00, 'ml', 0.02, 1.00, '细腻奶泡', 4, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (35, 10, '奶油芝士', 100.00, 'g', 0.08, 8.00, '进口奶油芝士', 1, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (36, 10, '消化饼干', 50.00, 'g', 0.02, 1.00, '消化饼干底', 2, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (37, 10, '鸡蛋', 1.00, '个', 1.50, 1.50, '新鲜鸡蛋', 3, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (38, 10, '细砂糖', 30.00, 'g', 0.01, 0.30, '细砂糖', 4, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `recipe` VALUES (39, 10, '香草精', 2.00, 'ml', 0.50, 1.00, '天然香草精', 5, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');

-- ----------------------------
-- Table structure for refund_request
-- ----------------------------
DROP TABLE IF EXISTS `refund_request`;
CREATE TABLE `refund_request`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '退款申请ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `customer_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '客户名称',
  `refund_amount` decimal(10, 2) NOT NULL COMMENT '退款金额',
  `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '退款原因',
  `status` int(11) NULL DEFAULT 0 COMMENT '状态：0-待处理，1-已同意，2-已拒绝',
  `reject_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  `process_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `deleted` int(11) NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '退款申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of refund_request
-- ----------------------------
INSERT INTO `refund_request` VALUES (1, 1, 'ORD202411190001', 2, '张三', 22.00, '商品质量问题，奶茶味道不对', 0, NULL, NULL, 0, '2024-11-19 10:30:00', '2025-11-19 18:18:32');
INSERT INTO `refund_request` VALUES (2, 3, 'ORD202411190003', 2, '张三', 26.00, '等待时间太长，不想要了', 0, NULL, NULL, 0, '2024-11-19 12:00:00', '2025-11-19 18:18:32');
INSERT INTO `refund_request` VALUES (3, 5, 'ORD202411190005', 2, '张三', 34.00, '配料不新鲜', 1, NULL, '2024-11-19 14:30:00', 0, '2024-11-19 14:00:00', '2025-11-19 18:18:32');
INSERT INTO `refund_request` VALUES (4, 8, 'ORD202411190008', 3, '李四', 30.00, '口味不符合预期', 2, '订单已完成且超过退款时限', '2024-11-19 17:00:00', 0, '2024-11-19 16:45:00', '2025-11-19 18:18:32');
INSERT INTO `refund_request` VALUES (5, 2, 'ORD202411190002', 3, '李四', 18.00, '下错单了', 0, NULL, NULL, 0, '2024-11-19 10:45:00', '2025-11-19 18:18:32');

-- ----------------------------
-- Table structure for review
-- ----------------------------
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `rating` int(11) NOT NULL COMMENT '评分：1-5星',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评价内容',
  `images` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片（JSON格式）',
  `is_anonymous` int(11) NULL DEFAULT 0 COMMENT '是否匿名：0-否，1-是',
  `deleted` int(11) NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of review
-- ----------------------------

-- ----------------------------
-- Table structure for staff
-- ----------------------------
DROP TABLE IF EXISTS `staff`;
CREATE TABLE `staff`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '员工ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码（MD5加密）',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '员工姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `employee_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '工号',
  `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'cashier' COMMENT '角色：super_admin-超级管理员，manager-店长，cashier-收银员，maker-制作员',
  `department` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '部门：management-管理部，front-前台部，kitchen-制作部，delivery-配送部',
  `hire_date` date NULL DEFAULT NULL COMMENT '入职时间',
  `salary` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '薪资',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
  `permissions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '权限（JSON格式）',
  `status` int(11) NULL DEFAULT 1 COMMENT '状态：0-离职，1-在职',
  `last_login` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `deleted` int(11) NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_employee_id`(`employee_id` ASC) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '员工表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of staff
-- ----------------------------
INSERT INTO `staff` VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '张三', '13800138001', 'zhangsan@milktea.com', NULL, 'EMP001', 'super_admin', 'management', '2023-01-15', 10000.00, '北京市朝阳区', '[\"dashboard.view\",\"dashboard.export\",\"product.view\",\"product.create\",\"product.edit\",\"product.delete\",\"order.view\",\"order.process\",\"order.refund\",\"user.view\",\"user.edit\",\"system.staff\",\"system.config\"]', 1, '2025-11-20 09:36:21', 0, '2025-11-20 09:36:21', '2025-11-20 09:36:21');
INSERT INTO `staff` VALUES (2, 'manager01', 'e10adc3949ba59abbe56e057f20f883e', '李四', '13800138002', 'lisi@milktea.com', NULL, 'EMP002', 'manager', 'management', '2023-03-20', 8000.00, '北京市海淀区', '[\"dashboard.view\",\"product.view\",\"product.edit\",\"order.view\",\"order.process\",\"user.view\"]', 1, '2025-11-19 09:36:21', 0, '2025-11-20 09:36:21', '2025-11-20 09:36:21');
INSERT INTO `staff` VALUES (3, 'cashier01', 'e10adc3949ba59abbe56e057f20f883e', '王五', '13800138003', 'wangwu@milktea.com', NULL, 'EMP003', 'cashier', 'front', '2023-05-10', 5000.00, '北京市西城区', '[\"order.view\",\"order.process\",\"product.view\"]', 1, '2025-11-20 07:36:21', 0, '2025-11-20 09:36:21', '2025-11-20 09:36:21');
INSERT INTO `staff` VALUES (4, 'maker01', 'e10adc3949ba59abbe56e057f20f883e', '赵六', '13800138004', 'zhaoliu@milktea.com', NULL, 'EMP004', 'maker', 'kitchen', '2023-06-15', 5500.00, '北京市东城区', '[\"order.view\",\"product.view\"]', 1, '2025-11-20 04:36:21', 0, '2025-11-20 09:36:21', '2025-11-20 09:36:21');
INSERT INTO `staff` VALUES (5, 'cashier02', 'e10adc3949ba59abbe56e057f20f883e', '孙七', '13800138005', 'sunqi@milktea.com', NULL, 'EMP005', 'cashier', 'front', '2023-08-01', 4800.00, '北京市丰台区', '[\"order.view\",\"order.process\",\"product.view\"]', 1, '2025-11-17 09:36:21', 0, '2025-11-20 09:36:21', '2025-11-20 09:36:21');
INSERT INTO `staff` VALUES (6, 'maker02', 'e10adc3949ba59abbe56e057f20f883e', '周八', '13800138006', 'zhouba@milktea.com', NULL, 'EMP006', 'maker', 'kitchen', '2023-09-20', 5200.00, '北京市石景山区', '[\"order.view\",\"product.view\"]', 0, '2025-10-21 09:36:21', 0, '2025-11-20 09:36:21', '2025-11-20 09:36:21');

-- ----------------------------
-- Table structure for store
-- ----------------------------
DROP TABLE IF EXISTS `store`;
CREATE TABLE `store`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '门店ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '门店名称',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '门店地址',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `manager` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '店长姓名',
  `manager_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '店长电话',
  `business_hours` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '营业时间（JSON格式，如：[\"09:00\",\"22:00\"]）',
  `latitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '纬度',
  `longitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '经度',
  `area` decimal(10, 2) NULL DEFAULT NULL COMMENT '门店面积（平方米）',
  `staff_count` int(11) NULL DEFAULT 0 COMMENT '员工数量',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '门店描述',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '门店图片（JSON格式）',
  `status` int(11) NULL DEFAULT 1 COMMENT '状态：0-关闭，1-营业',
  `deleted` int(11) NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '门店表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of store
-- ----------------------------
INSERT INTO `store` VALUES (1, '总店', '北京市朝阳区建国路88号', '010-12345678', '张三', '13800138001', '[\"09:00\",\"22:00\"]', 39.904989, 116.407526, 150.00, 15, '总店位于市中心，交通便利，环境优雅', NULL, 1, 0, '2025-11-20 09:40:24', '2025-11-20 09:40:24');
INSERT INTO `store` VALUES (2, '海淀分店', '北京市海淀区中关村大街1号', '010-23456789', '李四', '13800138002', '[\"09:00\",\"22:00\"]', 39.983424, 116.318977, 120.00, 10, '位于中关村核心区域，科技氛围浓厚', NULL, 1, 0, '2025-11-20 09:40:24', '2025-11-20 09:40:24');
INSERT INTO `store` VALUES (3, '西城分店', '北京市西城区西单北大街120号', '010-34567890', '王五', '13800138003', '[\"10:00\",\"21:00\"]', 39.913418, 116.374328, 100.00, 8, '西单商圈黄金位置，客流量大', NULL, 1, 0, '2025-11-20 09:40:24', '2025-11-20 09:40:24');
INSERT INTO `store` VALUES (4, '东城分店', '北京市东城区王府井大街138号', '010-45678901', '赵六', '13800138004', '[\"09:30\",\"21:30\"]', 39.909187, 116.416357, 130.00, 12, '王府井步行街旁，游客众多', NULL, 1, 0, '2025-11-20 09:40:24', '2025-11-20 09:40:24');
INSERT INTO `store` VALUES (5, '丰台分店', '北京市丰台区丰台路100号', '010-56789012', '孙七', '13800138005', '[\"09:00\",\"22:00\"]', 39.858427, 116.287123, 110.00, 9, '丰台区域中心店，服务周边社区', NULL, 0, 0, '2025-11-20 09:40:24', '2025-11-20 09:40:24');

-- ----------------------------
-- Table structure for system_config
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置键',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '配置值',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '配置描述',
  `type` int(11) NULL DEFAULT 1 COMMENT '配置类型：1-文本，2-数字，3-布尔，4-JSON',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_config_key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_config
-- ----------------------------
INSERT INTO `system_config` VALUES (1, 'shop_name', '奶茶小铺', '店铺名称', 1, '2025-11-20 09:47:37', '2025-11-20 09:47:37');
INSERT INTO `system_config` VALUES (2, 'shop_logo', '', '店铺Logo', 1, '2025-11-20 09:47:37', '2025-11-20 09:47:37');
INSERT INTO `system_config` VALUES (3, 'shop_phone', '400-123-4567', '联系电话', 1, '2025-11-20 09:47:37', '2025-11-20 09:47:37');
INSERT INTO `system_config` VALUES (4, 'shop_address', '北京市朝阳区建国路88号', '联系地址', 1, '2025-11-20 09:47:37', '2025-11-20 09:47:37');
INSERT INTO `system_config` VALUES (5, 'payment_wechat_enabled', 'true', '微信支付开关', 3, '2025-11-20 09:47:37', '2025-11-20 09:47:37');
INSERT INTO `system_config` VALUES (6, 'payment_alipay_enabled', 'false', '支付宝支付开关', 3, '2025-11-20 09:47:37', '2025-11-20 09:47:37');
INSERT INTO `system_config` VALUES (7, 'payment_balance_enabled', 'true', '余额支付开关', 3, '2025-11-20 09:47:37', '2025-11-20 09:47:37');
INSERT INTO `system_config` VALUES (8, 'order_auto_cancel_time', '30', '订单自动取消时间（分钟）', 2, '2025-11-20 09:47:37', '2025-11-20 09:47:37');
INSERT INTO `system_config` VALUES (9, 'order_prepare_time', '15', '预计制作时间（分钟）', 2, '2025-11-20 09:47:37', '2025-11-20 09:47:37');
INSERT INTO `system_config` VALUES (10, 'order_auto_complete_time', '24', '订单自动完成时间（小时）', 2, '2025-11-20 09:47:37', '2025-11-20 09:47:37');
INSERT INTO `system_config` VALUES (11, 'points_earn_rate', '1.0', '消费获取积分比例（消费1元获得积分）', 2, '2025-11-20 09:47:37', '2025-11-20 09:47:37');
INSERT INTO `system_config` VALUES (12, 'points_redeem_rate', '1.0', '积分抵扣比例（100积分可抵扣金额）', 2, '2025-11-20 09:47:37', '2025-11-20 09:47:37');
INSERT INTO `system_config` VALUES (13, 'points_min_redeem', '100', '最低抵扣积分', 2, '2025-11-20 09:47:37', '2025-11-20 09:47:37');
INSERT INTO `system_config` VALUES (14, 'member_upgrade_gold', '1000', '升级黄金会员消费金额', 2, '2025-11-20 09:47:37', '2025-11-20 09:47:37');
INSERT INTO `system_config` VALUES (15, 'member_upgrade_diamond', '5000', '升级钻石会员消费金额', 2, '2025-11-20 09:47:37', '2025-11-20 09:47:37');
INSERT INTO `system_config` VALUES (16, 'member_discount_gold', '0.95', '黄金会员折扣', 2, '2025-11-20 09:47:37', '2025-11-20 09:47:37');
INSERT INTO `system_config` VALUES (17, 'member_discount_diamond', '0.90', '钻石会员折扣', 2, '2025-11-20 09:47:37', '2025-11-20 09:47:37');
INSERT INTO `system_config` VALUES (18, 'system_maintenance', 'false', '系统维护模式', 3, '2025-11-20 09:47:37', '2025-11-20 09:47:37');
INSERT INTO `system_config` VALUES (19, 'system_register_enabled', 'true', '允许用户注册', 3, '2025-11-20 09:47:37', '2025-11-20 09:47:37');
INSERT INTO `system_config` VALUES (20, 'system_backup_auto', 'true', '自动备份开关', 3, '2025-11-20 09:47:37', '2025-11-20 09:47:37');
INSERT INTO `system_config` VALUES (21, 'system_backup_time', '03:00', '自动备份时间', 1, '2025-11-20 09:47:37', '2025-11-20 09:47:37');
INSERT INTO `system_config` VALUES (22, 'system_last_backup', '2025-11-20 14:26:44', '最后备份时间', 1, '2025-11-20 09:47:37', '2025-11-20 09:47:37');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码（MD5加密）',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `gender` int(11) NULL DEFAULT NULL COMMENT '性别：1-男，2-女',
  `birthday` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '生日',
  `openid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信openid',
  `member_level` int(11) NULL DEFAULT 0 COMMENT '会员等级：0-普通会员，1-黄金会员，2-钻石会员',
  `member_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会员号',
  `invite_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邀请码',
  `inviter_id` bigint(20) NULL DEFAULT NULL COMMENT '邀请人ID',
  `invite_time` datetime NULL DEFAULT NULL COMMENT '被邀请时间',
  `points` int(11) NULL DEFAULT 0 COMMENT '积分',
  `balance` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '余额',
  `user_type` int(11) NULL DEFAULT 0 COMMENT '用户类型：0-普通用户，1-管理员',
  `status` int(11) NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `deleted` int(11) NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_member_no`(`member_no` ASC) USING BTREE,
  UNIQUE INDEX `uk_invite_code`(`invite_code` ASC) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_openid`(`openid` ASC) USING BTREE,
  INDEX `idx_inviter_id`(`inviter_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '0192023a7bbd73250516f069df18b500', '系统管理员', NULL, NULL, 1, '1988-08-10', NULL, 0, 'MT20251119000001', 'MVAHFC', NULL, NULL, 0, 0.00, 1, 1, 0, '2025-11-19 18:18:32', '2025-11-26 19:10:37', '2025-11-20 10:10:47');
INSERT INTO `user` VALUES (2, 'user1', 'e10adc3949ba59abbe56e057f20f883e', '张三', NULL, '13800138000', 1, '1990-01-15', NULL, 0, 'MT20251119000002', '8PMRKC', NULL, NULL, 100, 50.00, 0, 1, 0, '2025-11-19 18:18:32', '2025-11-26 19:10:37', '2025-11-20 11:55:20');
INSERT INTO `user` VALUES (3, 'user2', 'e10adc3949ba59abbe56e057f20f883e', '李四', NULL, '13900139000', 2, '1992-05-20', NULL, 0, 'MT20251119000003', 'P8RVT8', NULL, NULL, 500, 100.00, 0, 1, 0, '2025-11-19 18:18:32', '2025-11-26 19:10:37', NULL);
INSERT INTO `user` VALUES (4, 'user3', 'e10adc3949ba59abbe56e057f20f883e', '王五', NULL, '13700137000', NULL, NULL, NULL, 0, 'MT20241101000004', 'F7CXCP', NULL, NULL, 50, 20.00, 0, 1, 0, '2024-11-01 10:00:00', '2025-11-26 19:10:37', '2024-11-18 15:30:00');
INSERT INTO `user` VALUES (5, 'user4', 'e10adc3949ba59abbe56e057f20f883e', '赵六', NULL, '13600136000', NULL, NULL, NULL, 0, 'MT20241105000005', '453RAU', NULL, NULL, 80, 35.50, 0, 1, 0, '2024-11-05 14:20:00', '2025-11-26 19:10:37', '2024-11-19 09:15:00');
INSERT INTO `user` VALUES (6, 'user5', 'e10adc3949ba59abbe56e057f20f883e', '孙七', NULL, '13500135000', NULL, NULL, NULL, 0, 'MT20241108000006', '4PYEX6', NULL, NULL, 120, 50.00, 0, 1, 0, '2024-11-08 16:45:00', '2025-11-26 19:10:37', '2024-11-19 11:20:00');
INSERT INTO `user` VALUES (7, 'user6', 'e10adc3949ba59abbe56e057f20f883e', '周八', NULL, '13400134000', NULL, NULL, NULL, 0, 'MT20241110000007', 'RYABG4', NULL, NULL, 30, 15.00, 0, 1, 0, '2024-11-10 09:30:00', '2025-11-26 19:10:37', '2024-11-17 14:00:00');
INSERT INTO `user` VALUES (8, 'user7', 'e10adc3949ba59abbe56e057f20f883e', '吴九', NULL, '13300133000', NULL, NULL, NULL, 0, 'MT20241112000008', 'TK5NMX', NULL, NULL, 60, 25.00, 0, 1, 0, '2024-11-12 11:15:00', '2025-11-26 19:10:37', '2024-11-19 10:45:00');
INSERT INTO `user` VALUES (9, 'vip1', 'e10adc3949ba59abbe56e057f20f883e', 'VIP张三', NULL, '13200132000', NULL, NULL, NULL, 1, 'MT20241015000009', 'F8GDA2', NULL, NULL, 800, 200.00, 0, 1, 0, '2024-10-15 10:00:00', '2025-11-26 19:10:37', '2024-11-19 08:30:00');
INSERT INTO `user` VALUES (10, 'vip2', 'e10adc3949ba59abbe56e057f20f883e', 'VIP李四', NULL, '13100131000', NULL, NULL, NULL, 1, 'MT20241020000010', 'ZNYP8Q', NULL, NULL, 650, 150.00, 0, 1, 0, '2024-10-20 14:30:00', '2025-11-26 19:10:37', '2024-11-19 12:00:00');
INSERT INTO `user` VALUES (11, 'vip3', 'e10adc3949ba59abbe56e057f20f883e', 'VIP王五', NULL, '13000130000', NULL, NULL, NULL, 1, 'MT20241025000011', 'RC7NFZ', NULL, NULL, 720, 180.00, 0, 1, 0, '2024-10-25 16:00:00', '2025-11-26 19:10:37', '2024-11-18 16:30:00');
INSERT INTO `user` VALUES (12, 'vip4', 'e10adc3949ba59abbe56e057f20f883e', 'VIP赵六', NULL, '12900129000', NULL, NULL, NULL, 1, 'MT20241101000012', 'CG3MN7', NULL, NULL, 580, 120.00, 0, 1, 0, '2024-11-01 09:00:00', '2025-11-26 19:10:37', '2024-11-19 14:15:00');
INSERT INTO `user` VALUES (13, 'svip1', 'e10adc3949ba59abbe56e057f20f883e', 'SVIP张总', NULL, '12800128000', NULL, NULL, NULL, 2, 'MT20240901000013', 'NEWYWD', NULL, NULL, 2500, 500.00, 0, 1, 0, '2024-09-01 10:00:00', '2025-11-26 19:10:37', '2024-11-19 09:00:00');
INSERT INTO `user` VALUES (14, 'svip2', 'e10adc3949ba59abbe56e057f20f883e', 'SVIP李', NULL, '12700127000', NULL, NULL, NULL, 2, 'MT20240915000014', 'UTCZNZ', NULL, NULL, 2200, 450.00, 0, 1, 0, '2024-09-15 14:00:00', '2025-11-26 19:10:37', '2024-11-19 11:30:00');
INSERT INTO `user` VALUES (15, 'svip3', 'e10adc3949ba59abbe56e057f20f883e', 'SVIP王总', NULL, '12600126000', NULL, NULL, NULL, 2, 'MT20241001000015', 'SMKM8Y', NULL, NULL, 1800, 380.00, 0, 1, 0, '2024-10-01 16:00:00', '2025-11-26 19:10:37', '2024-11-18 15:00:00');
INSERT INTO `user` VALUES (16, 'newuser1', 'e10adc3949ba59abbe56e057f20f883e', '新用户1', NULL, '12500125000', NULL, NULL, NULL, 1, 'MT20251119000016', 'U2FSBY', NULL, NULL, 0, 0.00, 0, 1, 0, '2025-11-19 18:22:58', '2025-11-26 19:10:37', NULL);
INSERT INTO `user` VALUES (17, 'newuser2', 'e10adc3949ba59abbe56e057f20f883e', '新用户2', NULL, '12400124000', NULL, NULL, NULL, 0, 'MT20251119000017', 'MWF7F9', NULL, NULL, 0, 0.00, 0, 1, 0, '2025-11-19 18:22:58', '2025-11-26 19:10:37', NULL);
INSERT INTO `user` VALUES (18, 'newuser3', 'e10adc3949ba59abbe56e057f20f883e', '新用户3', NULL, '12300123000', NULL, NULL, NULL, 0, 'MT20251119000018', 'STFHVM', NULL, NULL, 0, 0.00, 0, 1, 0, '2025-11-19 18:22:58', '2025-11-26 19:10:37', NULL);
INSERT INTO `user` VALUES (19, 'disabled1', 'e10adc3949ba59abbe56e057f20f883e', '禁用用户1', NULL, '12200122000', NULL, NULL, NULL, 0, 'MT20241101000019', '7WM6PM', NULL, NULL, 20, 10.00, 0, 0, 0, '2024-11-01 10:00:00', '2025-11-26 19:10:37', '2024-11-10 15:00:00');
INSERT INTO `user` VALUES (20, 'disabled2', 'e10adc3949ba59abbe56e057f20f883e', '禁用用户2', NULL, '12100121000', NULL, NULL, NULL, 0, 'MT20241105000020', 'WAF64T', NULL, NULL, 15, 5.00, 0, 0, 0, '2024-11-05 14:00:00', '2025-11-26 19:10:37', '2024-11-12 10:00:00');
INSERT INTO `user` VALUES (21, '15045678901', 'e10adc3949ba59abbe56e057f20f883e', '理蛋坤', 'http://localhost:8080/api/uploads/avatars/avatar_f08ad1db-ffb5-4c9f-9b09-34650fdc3825.jpg', '15045678901', 1, '2000-01-01', NULL, 2, 'MT20251120000021', 'JXTZ93', NULL, NULL, 10000, 95888.00, 0, 1, 0, '2025-11-20 14:38:50', '2025-11-26 19:10:37', '2025-11-20 14:55:20');

-- ----------------------------
-- Table structure for user_address
-- ----------------------------
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '城市',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '区县',
  `detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  `tag` int(11) NULL DEFAULT NULL COMMENT '地址标签：1-家庭，2-公司，3-学校，4-其他',
  `is_default` int(11) NULL DEFAULT 0 COMMENT '是否默认：0-否，1-是',
  `deleted` int(11) NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户地址表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_address
-- ----------------------------
INSERT INTO `user_address` VALUES (1, 2, '张三', '13800138000', '广东省', '深圳市', '南山区', '科技园南区', 1, 1, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `user_address` VALUES (2, 2, '张三', '13800138000', '广东省', '深圳市', '福田区', '华强北商圈', 2, 0, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `user_address` VALUES (3, 3, '李四', '13900139000', '北京市', '北京市', '朝阳区', '三里屯', 1, 1, 0, '2025-11-19 18:18:32', '2025-11-19 18:18:32');
INSERT INTO `user_address` VALUES (4, 21, '张三', '15000000000', '广东省', '深圳市', '南山区', '11111', 2, 0, 0, '2025-11-26 16:41:36', '2025-11-26 16:41:36');
INSERT INTO `user_address` VALUES (5, 21, '李乐样', '19198230823', '北京市', '市辖区', '市辖区', '6666', 3, 0, 0, '2025-11-26 16:53:37', '2025-11-26 16:53:37');

-- ----------------------------
-- Table structure for user_coupon
-- ----------------------------
DROP TABLE IF EXISTS `user_coupon`;
CREATE TABLE `user_coupon`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户优惠券ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `coupon_id` bigint(20) NOT NULL COMMENT '优惠券ID',
  `status` int(11) NULL DEFAULT 0 COMMENT '状态：0-未使用，1-已使用，2-已过期',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT '使用的订单ID',
  `use_time` datetime NULL DEFAULT NULL COMMENT '使用时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_coupon_id`(`coupon_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户优惠券表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_coupon
-- ----------------------------
INSERT INTO `user_coupon` VALUES (1, 2, 1, 0, NULL, NULL, '2025-11-19 18:18:32');
INSERT INTO `user_coupon` VALUES (2, 2, 2, 0, NULL, NULL, '2025-11-19 18:18:32');
INSERT INTO `user_coupon` VALUES (3, 3, 1, 0, NULL, NULL, '2025-11-19 18:18:32');
INSERT INTO `user_coupon` VALUES (4, 3, 3, 1, NULL, NULL, '2025-11-19 18:18:32');
INSERT INTO `user_coupon` VALUES (5, 21, 1, 0, NULL, NULL, '2025-11-26 17:33:49');
INSERT INTO `user_coupon` VALUES (6, 21, 3, 0, NULL, NULL, '2025-11-26 17:33:53');
INSERT INTO `user_coupon` VALUES (7, 21, 2, 0, NULL, NULL, '2025-11-26 17:33:54');
INSERT INTO `user_coupon` VALUES (8, 2, 1, 0, NULL, NULL, '2025-11-26 17:41:28');
INSERT INTO `user_coupon` VALUES (9, 2, 2, 0, NULL, NULL, '2025-11-26 17:41:28');
INSERT INTO `user_coupon` VALUES (10, 2, 6, 0, NULL, NULL, '2025-11-26 17:41:28');
INSERT INTO `user_coupon` VALUES (11, 2, 7, 0, NULL, NULL, '2025-11-26 17:41:28');
INSERT INTO `user_coupon` VALUES (12, 2, 11, 0, NULL, NULL, '2025-11-26 17:41:28');
INSERT INTO `user_coupon` VALUES (13, 2, 3, 1, NULL, NULL, '2025-11-21 17:41:28');
INSERT INTO `user_coupon` VALUES (14, 2, 4, 1, NULL, NULL, '2025-11-16 17:41:28');
INSERT INTO `user_coupon` VALUES (15, 2, 5, 2, NULL, NULL, '2025-10-27 17:41:28');
INSERT INTO `user_coupon` VALUES (16, 3, 1, 0, NULL, NULL, '2025-11-26 17:41:28');
INSERT INTO `user_coupon` VALUES (17, 3, 2, 0, NULL, NULL, '2025-11-26 17:41:28');
INSERT INTO `user_coupon` VALUES (18, 3, 8, 0, NULL, NULL, '2025-11-26 17:41:28');
INSERT INTO `user_coupon` VALUES (19, 3, 9, 0, NULL, NULL, '2025-11-26 17:41:28');
INSERT INTO `user_coupon` VALUES (20, 3, 3, 1, NULL, NULL, '2025-11-23 17:41:28');
INSERT INTO `user_coupon` VALUES (21, 3, 6, 2, NULL, NULL, '2025-11-06 17:41:28');
INSERT INTO `user_coupon` VALUES (22, 21, 14, 0, NULL, NULL, '2025-11-26 17:42:39');
INSERT INTO `user_coupon` VALUES (23, 21, 4, 0, NULL, NULL, '2025-11-26 17:44:10');
INSERT INTO `user_coupon` VALUES (24, 21, 5, 0, NULL, NULL, '2025-11-26 17:44:16');
INSERT INTO `user_coupon` VALUES (25, 21, 6, 0, NULL, NULL, '2025-11-26 17:44:17');

-- ----------------------------
-- Table structure for user_settings
-- ----------------------------
DROP TABLE IF EXISTS `user_settings`;
CREATE TABLE `user_settings`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `order_notification` int(11) NULL DEFAULT 1 COMMENT '订单通知：0-关闭 1-开启',
  `activity_push` int(11) NULL DEFAULT 1 COMMENT '活动推送：0-关闭 1-开启',
  `coupon_reminder` int(11) NULL DEFAULT 1 COMMENT '优惠提醒：0-关闭 1-开启',
  `personalized_recommend` int(11) NULL DEFAULT 1 COMMENT '个性化推荐：0-关闭 1-开启',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户设置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_settings
-- ----------------------------
INSERT INTO `user_settings` VALUES (1, 21, 1, 1, 1, 1, '2025-11-26 20:22:46', '2025-11-26 20:23:08');

-- ----------------------------
-- Procedure structure for update_member_level
-- ----------------------------
DROP PROCEDURE IF EXISTS `update_member_level`;
delimiter ;;
CREATE PROCEDURE `update_member_level`(IN p_user_id BIGINT)
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
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
