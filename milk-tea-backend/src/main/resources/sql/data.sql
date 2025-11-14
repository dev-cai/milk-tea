-- 奶茶小程序示例数据
USE milk_tea;

-- 插入管理员用户
INSERT INTO `user` (`username`, `password`, `nickname`, `user_type`, `status`) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxp4l7F2uIkrMRm', '系统管理员', 1, 1);

-- 插入普通用户
INSERT INTO `user` (`username`, `password`, `nickname`, `member_level`, `points`, `balance`, `user_type`, `status`) VALUES 
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxp4l7F2uIkrMRm', '张三', 0, 100, 50.00, 0, 1),
('user2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxp4l7F2uIkrMRm', '李四', 1, 500, 100.00, 0, 1);

-- 插入商品分类
INSERT INTO `category` (`name`, `parent_id`, `icon`, `sort`, `status`) VALUES 
('茶饮', 0, 'tea-icon', 1, 1),
('咖啡', 0, 'coffee-icon', 2, 1),
('小食', 0, 'snack-icon', 3, 1);

-- 插入商品
INSERT INTO `product` (`name`, `category_id`, `description`, `image`, `price`, `member_price`, `cost`, `stock`, `sales`, `nutrition`, `status`, `is_recommend`, `sort`) VALUES 
('珍珠奶茶', 1, '经典珍珠奶茶，香滑浓郁', 'product1.jpg', 12.00, 10.80, 8.00, 100, 156, '热量：200kcal', 1, 1, 1),
('红豆奶茶', 1, '香甜红豆配奶茶', 'product2.jpg', 14.00, 12.60, 9.00, 80, 89, '热量：220kcal', 1, 1, 2),
('抹茶拿铁', 2, '浓郁抹茶香味', 'product3.jpg', 18.00, 16.20, 12.00, 60, 45, '热量：180kcal', 1, 0, 3),
('美式咖啡', 2, '经典美式咖啡', 'product4.jpg', 15.00, 13.50, 10.00, 90, 67, '热量：5kcal', 1, 0, 4),
('芝士蛋糕', 3, '香滑芝士蛋糕', 'product5.jpg', 25.00, 22.50, 15.00, 30, 23, '热量：350kcal', 1, 1, 5);

-- 插入优惠券
INSERT INTO `coupon` (`name`, `type`, `discount`, `min_amount`, `total_count`, `received_count`, `valid_start`, `valid_end`, `status`) VALUES 
('新用户专享券', 1, 5.00, 20.00, 1000, 156, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 1),
('满50减10', 1, 10.00, 50.00, 500, 89, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 1),
('9折优惠券', 2, 0.90, 30.00, 300, 45, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 1);

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
