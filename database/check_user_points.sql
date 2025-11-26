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
