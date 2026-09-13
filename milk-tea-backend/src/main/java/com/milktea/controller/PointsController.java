package com.milktea.controller;

import com.milktea.common.PageResult;
import com.milktea.common.Result;
import com.milktea.entity.PointsHistory;
import com.milktea.entity.PointsProduct;
import com.milktea.service.PointsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 积分控制器
 * @author MilkTea Team
 */
@Slf4j
@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointsController {
    
    private final PointsService pointsService;
    
    /**
     * 获取积分历史
     */
    @GetMapping("/history")
    public Result<PageResult<PointsHistory>> getHistory(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size, HttpServletRequest request) {
        PageResult<PointsHistory> result = pointsService.getHistory(verifyUser(userId, request), page, size);
        return Result.success(result);
    }
    
    /**
     * 获取积分商品列表
     */
    @GetMapping("/products")
    public Result<PageResult<PointsProduct>> getProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        PageResult<PointsProduct> result = pointsService.getProducts(page, size);
        return Result.success(result);
    }
    
    /**
     * 积分兑换
     */
    @PostMapping("/exchange")
    public Result<String> exchange(@RequestBody Map<String, Object> request, HttpServletRequest servletRequest) {
        try {
            Long userId = Long.valueOf(servletRequest.getAttribute("authenticatedUserId").toString());
            Long productId = Long.valueOf(request.get("productId").toString());
            Integer quantity = Integer.valueOf(request.getOrDefault("quantity", 1).toString());
            
            pointsService.exchange(userId, productId, quantity);
            return Result.success("兑换成功");
        } catch (Exception e) {
            log.error("积分兑换失败", e);
            if (e instanceof com.milktea.exception.BusinessException) throw (com.milktea.exception.BusinessException) e;
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 每日签到
     */
    @PostMapping("/checkin")
    public Result<String> checkin(@RequestBody Map<String, Object> request, HttpServletRequest servletRequest) {
        try {
            Long userId = Long.valueOf(servletRequest.getAttribute("authenticatedUserId").toString());
            pointsService.checkin(userId);
            return Result.success("签到成功，获得10积分");
        } catch (Exception e) {
            log.error("签到失败", e);
            if (e instanceof com.milktea.exception.BusinessException) throw (com.milktea.exception.BusinessException) e;
            return Result.error(e.getMessage());
        }
    }

    private Long verifyUser(Long suppliedUserId, HttpServletRequest request) {
        Object id = request.getAttribute("authenticatedUserId");
        if (id == null) throw new org.springframework.security.access.AccessDeniedException("请先登录");
        Long current = Long.valueOf(id.toString());
        if (!current.equals(suppliedUserId)) throw new org.springframework.security.access.AccessDeniedException("无权操作其他用户数据");
        return current;
    }
}
