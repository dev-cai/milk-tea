-- 测试优惠券数据查询

USE milk_tea;

-- 1. 查看所有可领取的优惠券
SELECT 
    id, name, type, discount, min_amount, 
    total_count, received_count, 
    valid_start, valid_end, status
FROM coupon 
WHERE status = 1 
  AND valid_end > NOW()
ORDER BY create_time DESC;

-- 2. 查看用户优惠券（假设用户ID为2）
SELECT 
    uc.id, 
    uc.user_id, 
    uc.coupon_id, 
    uc.status, 
    uc.create_time,
    c.name as coupon_name,
    c.type,
    c.discount,
    c.min_amount
FROM user_coupon uc
LEFT JOIN coupon c ON uc.coupon_id = c.id
WHERE uc.user_id = 2
ORDER BY uc.create_time DESC;

-- 3. 查看所有用户的优惠券统计
SELECT 
    u.id as user_id,
    u.username,
    COUNT(uc.id) as coupon_count,
    SUM(CASE WHEN uc.status = 0 THEN 1 ELSE 0 END) as unused_count,
    SUM(CASE WHEN uc.status = 1 THEN 1 ELSE 0 END) as used_count,
    SUM(CASE WHEN uc.status = 2 THEN 1 ELSE 0 END) as expired_count
FROM user u
LEFT JOIN user_coupon uc ON u.id = uc.user_id
WHERE u.user_type = 0
GROUP BY u.id, u.username
HAVING coupon_count > 0
ORDER BY coupon_count DESC;

-- 4. 测试关联查询（模拟后端SQL）
SELECT 
    uc.id, 
    uc.user_id as userId, 
    uc.coupon_id as couponId, 
    uc.status, 
    uc.order_id as orderId, 
    uc.use_time as useTime, 
    uc.create_time as createTime, 
    c.name, 
    c.type, 
    c.discount, 
    c.min_amount as minAmount, 
    c.valid_start as startTime, 
    c.valid_end as endTime, 
    CONCAT('满', c.min_amount, '元可用') as description 
FROM user_coupon uc 
LEFT JOIN coupon c ON uc.coupon_id = c.id 
WHERE uc.user_id = 2
ORDER BY uc.create_time DESC;

-- 5. 检查是否有过期的优惠券需要更新
SELECT 
    uc.id,
    uc.user_id,
    uc.status,
    c.name,
    c.valid_end,
    CASE 
        WHEN c.valid_end < NOW() THEN '已过期'
        ELSE '未过期'
    END as expire_status
FROM user_coupon uc
LEFT JOIN coupon c ON uc.coupon_id = c.id
WHERE uc.status = 0
  AND c.valid_end < NOW();
