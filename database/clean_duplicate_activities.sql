-- 清理重复的营销活动数据
USE `milk_tea`;

-- 删除所有"新品抄茶"和"买一送一"活动
DELETE FROM `marketing_activity` WHERE `name` IN ('新品抄茶', '新品炒茶', '买一送一');

-- 查看剩余的活动
SELECT * FROM `marketing_activity` WHERE `status` = 1 AND `deleted` = 0;
