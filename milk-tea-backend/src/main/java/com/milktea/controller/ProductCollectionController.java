package com.milktea.controller;

import com.milktea.common.Result;
import com.milktea.entity.Product;
import com.milktea.service.ProductCollectionService;
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
    public Result<String> collectProduct(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        return collectionService.collectProduct(id, userId);
    }
    
    /**
     * 取消收藏
     */
    @DeleteMapping("/{id}/uncollect")
    public Result<String> uncollectProduct(@PathVariable Long id, @RequestParam Long userId) {
        return collectionService.uncollectProduct(id, userId);
    }
    
    /**
     * 获取收藏列表
     */
    @GetMapping("/collections")
    public Result<List<Product>> getCollections(@RequestParam Long userId) {
        return collectionService.getCollections(userId);
    }
    
    /**
     * 检查是否已收藏
     */
    @GetMapping("/{id}/is-collected")
    public Result<Boolean> isCollected(@PathVariable Long id, @RequestParam Long userId) {
        boolean collected = collectionService.isCollected(id, userId);
        return Result.success(collected);
    }
}
