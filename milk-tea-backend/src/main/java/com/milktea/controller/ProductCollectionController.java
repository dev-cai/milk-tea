package com.milktea.controller;

import com.milktea.common.Result;
import com.milktea.entity.Product;
import com.milktea.service.ProductCollectionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品收藏控制器
 * @author MilkTea Team
 */
@Slf4j
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductCollectionController {
    
    private final ProductCollectionService collectionService;
    
    /**
     * 收藏商品
     */
    @PostMapping("/{id}/collect")
    public Result<String> collectProduct(@PathVariable Long id, @RequestBody Map<String, Object> request,
                                         HttpServletRequest servletRequest) {
        Long userId = verifyUser(request.get("userId"), servletRequest);
        return collectionService.collectProduct(id, userId);
    }
    
    /**
     * 取消收藏
     */
    @DeleteMapping("/{id}/uncollect")
    public Result<String> uncollectProduct(@PathVariable Long id, @RequestParam Long userId, HttpServletRequest request) {
        return collectionService.uncollectProduct(id, verifyUser(userId, request));
    }
    
    /**
     * 获取收藏列表
     */
    @GetMapping("/collections")
    public Result<List<Product>> getCollections(@RequestParam Long userId, HttpServletRequest request) {
        return collectionService.getCollections(verifyUser(userId, request));
    }
    
    /**
     * 检查是否已收藏
     */
    @GetMapping("/{id}/is-collected")
    public Result<Boolean> isCollected(@PathVariable Long id, @RequestParam Long userId, HttpServletRequest request) {
        boolean collected = collectionService.isCollected(id, verifyUser(userId, request));
        return Result.success(collected);
    }

    private Long verifyUser(Object suppliedUserId, HttpServletRequest request) {
        Object authenticatedUserId = request.getAttribute("authenticatedUserId");
        if (authenticatedUserId == null) {
            throw new org.springframework.security.access.AccessDeniedException("未登录");
        }
        Long currentUserId = Long.valueOf(authenticatedUserId.toString());
        if (suppliedUserId != null && !currentUserId.equals(Long.valueOf(suppliedUserId.toString()))) {
            throw new org.springframework.security.access.AccessDeniedException("无权操作其他用户数据");
        }
        return currentUserId;
    }
}
