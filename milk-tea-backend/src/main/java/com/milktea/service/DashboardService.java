package com.milktea.service;

import java.util.List;
import java.util.Map;

/**
 * 仪表盘服务接口
 * @author MilkTea Team
 */
public interface DashboardService {
    
    /**
     * 获取仪表盘统计数据
     * @return 统计数据
     */
    Map<String, Object> getDashboardStats();
    
    /**
     * 获取销售趋势数据
     * @param days 天数
     * @return 销售趋势数据
     */
    Map<String, Object> getSalesTrend(Integer days);
    
    /**
     * 获取商品销售排行
     * @param limit 限制数量
     * @return 商品排行数据
     */
    Map<String, Object> getProductRanking(Integer limit);
    
    /**
     * 获取用户增长数据
     * @param days 天数
     * @return 用户增长数据
     */
    Map<String, Object> getUserGrowth(Integer days);
    
    /**
     * 获取预警信息
     * @return 预警信息列表
     */
    List<Map<String, Object>> getAlerts();
    
    /**
     * 获取订单状态分布
     * @return 订单状态分布数据
     */
    Map<String, Object> getOrderStatus();
}
