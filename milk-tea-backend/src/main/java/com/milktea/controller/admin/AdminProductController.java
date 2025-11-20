package com.milktea.controller.admin;

import com.milktea.common.PageResult;
import com.milktea.common.Result;
import com.milktea.entity.Product;
import com.milktea.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理端商品控制器
 * @author MilkTea Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/product")
public class AdminProductController {
    
    private final ProductService productService;
    
    public AdminProductController(ProductService productService) {
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
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String stockStatus) {
        return productService.getAdminProductPage(page, size, categoryId, keyword, status, stockStatus);
    }
    
    /**
     * 获取商品详情
     */
    @GetMapping("/{id}")
    public Result<Product> getProductById(@PathVariable Long id) {
        return productService.getAdminProductById(id);
    }
    
    /**
     * 添加商品
     */
    @PostMapping
    public Result<String> addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }
    
    /**
     * 更新商品
     */
    @PutMapping("/{id}")
    public Result<String> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        return productService.updateProduct(product);
    }
    
    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }
    
    /**
     * 批量删除商品
     */
    @DeleteMapping("/batch")
    public Result<String> batchDeleteProducts(@RequestBody List<Long> ids) {
        return productService.batchDeleteProducts(ids);
    }
    
    /**
     * 更新商品状态
     */
    @PutMapping("/{id}/status")
    public Result<String> updateProductStatus(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        Integer status = request.get("status");
        return productService.updateProductStatus(id, status);
    }
    
    /**
     * 批量更新商品状态
     */
    @PutMapping("/batch/status")
    public Result<String> batchUpdateProductStatus(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) request.get("ids");
        Integer status = (Integer) request.get("status");
        return productService.batchUpdateProductStatus(ids, status);
    }
    
    /**
     * 更新商品库存
     */
    @PutMapping("/{id}/stock")
    public Result<String> updateProductStock(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Integer stock = (Integer) request.get("stock");
        String operation = (String) request.get("operation"); // add, subtract, set
        return productService.updateProductStock(id, stock, operation);
    }
    
    /**
     * 批量更新商品库存
     */
    @PutMapping("/batch/stock")
    public Result<String> batchUpdateProductStock(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");
        return productService.batchUpdateProductStock(items);
    }
    
    /**
     * 获取库存预警商品
     */
    @GetMapping("/stock/alerts")
    public Result<List<Product>> getStockAlerts(@RequestParam(defaultValue = "10") Integer threshold) {
        return productService.getStockAlerts(threshold);
    }
    
    /**
     * 获取库存统计
     */
    @GetMapping("/stock/statistics")
    public Result<Map<String, Object>> getStockStatistics() {
        return productService.getStockStatistics();
    }
}
