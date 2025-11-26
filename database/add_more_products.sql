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
