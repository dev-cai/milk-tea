package com.milktea.controller;

import com.milktea.common.PageResult;
import com.milktea.common.Result;
import com.milktea.entity.Category;
import com.milktea.entity.Product;
import com.milktea.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器
 * @author MilkTea Team
 */
@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    /**
     * 分页查询商品
     */
    @GetMapping("/page")
    public Result<PageResult<Product>> getProductPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        return productService.getProductPage(page, size, categoryId, keyword);
    }
    
    /**
     * 获取商品详情
     */
    @GetMapping("/{id}")
    public Result<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }
    
    /**
     * 获取推荐商品
     */
    @GetMapping("/recommend")
    public Result<List<Product>> getRecommendProducts(@RequestParam(defaultValue = "10") Integer limit) {
        return productService.getRecommendProducts(limit);
    }
    
    /**
     * 获取热销商品
     */
    @GetMapping("/hot")
    public Result<List<Product>> getHotProducts(@RequestParam(defaultValue = "10") Integer limit) {
        return productService.getHotProducts(limit);
    }
    
    /**
     * 获取商品分类
     */
    @GetMapping("/categories")
    public Result<List<Category>> getCategories() {
        return productService.getCategories();
    }
}
