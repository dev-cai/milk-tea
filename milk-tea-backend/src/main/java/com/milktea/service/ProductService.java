package com.milktea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.milktea.common.PageResult;
import com.milktea.common.Result;
import com.milktea.entity.Category;
import com.milktea.entity.Product;
import com.milktea.exception.BusinessException;
import com.milktea.mapper.CategoryMapper;
import com.milktea.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品服务类
 * @author MilkTea Team
 */
@Slf4j
@Service
public class ProductService {
    
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    
    public ProductService(ProductMapper productMapper, CategoryMapper categoryMapper) {
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
    }
    
    /**
     * 分页查询商品（用户端）
     */
    public Result<PageResult<Product>> getProductPage(Integer page, Integer size, Long categoryId, String keyword) {
        Page<Product> pageObj = new Page<>(page, size);
        
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getStatus, 1) // 只查询上架商品
                   .eq(Product::getDeleted, 0);
        
        if (categoryId != null) {
            queryWrapper.eq(Product::getCategoryId, categoryId);
        }
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(Product::getName, keyword)
                       .or()
                       .like(Product::getDescription, keyword);
        }
        
        queryWrapper.orderByDesc(Product::getSort)
                   .orderByDesc(Product::getCreateTime);
        
        Page<Product> result = productMapper.selectPage(pageObj, queryWrapper);
        
        PageResult<Product> pageResult = new PageResult<>(
            result.getRecords(),
            result.getTotal(),
            result.getCurrent(),
            result.getSize()
        );
        
        return Result.success("查询成功", pageResult);
    }
    
    /**
     * 分页查询商品（管理端）
     */
    public Result<PageResult<Product>> getAdminProductPage(Integer page, Integer size, Long categoryId, String keyword, Integer status, String stockStatus) {
        Page<Product> pageObj = new Page<>(page, size);
        
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getDeleted, 0); // 只查询未删除的商品
        
        if (categoryId != null) {
            queryWrapper.eq(Product::getCategoryId, categoryId);
        }
        
        if (status != null) {
            queryWrapper.eq(Product::getStatus, status);
        }
        
        // 处理库存状态筛选
        if (StringUtils.hasText(stockStatus)) {
            switch (stockStatus) {
                case "zero":
                    // 零库存
                    queryWrapper.eq(Product::getStock, 0);
                    break;
                case "low":
                    // 库存不足（1-10）
                    queryWrapper.gt(Product::getStock, 0)
                               .le(Product::getStock, 10);
                    break;
                case "sufficient":
                    // 库存充足（>50）
                    queryWrapper.gt(Product::getStock, 50);
                    break;
            }
        }
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(Product::getName, keyword)
                       .or()
                       .like(Product::getDescription, keyword);
        }
        
        queryWrapper.orderByDesc(Product::getSort)
                   .orderByDesc(Product::getCreateTime);
        
        Page<Product> result = productMapper.selectPage(pageObj, queryWrapper);
        
        // 为每个商品添加分类名称
        for (Product product : result.getRecords()) {
            if (product.getCategoryId() != null) {
                Category category = categoryMapper.selectById(product.getCategoryId());
                if (category != null) {
                    product.setCategoryName(category.getName());
                }
            }
        }
        
        PageResult<Product> pageResult = new PageResult<>(
            result.getRecords(),
            result.getTotal(),
            result.getCurrent(),
            result.getSize()
        );
        
        return Result.success("查询成功", pageResult);
    }
    
    /**
     * 获取商品详情（用户端）
     */
    public Result<Product> getProductById(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null || product.getStatus() == 0) {
            throw new BusinessException("商品不存在或已下架");
        }
        return Result.success("获取成功", product);
    }
    
    /**
     * 获取商品详情（管理端）
     */
    public Result<Product> getAdminProductById(Long id) {
        try {
            Product product = productMapper.selectById(id);
            if (product == null || product.getDeleted() == 1) {
                throw new BusinessException("商品不存在");
            }
            
            // 添加分类名称
            if (product.getCategoryId() != null) {
                Category category = categoryMapper.selectById(product.getCategoryId());
                if (category != null) {
                    product.setCategoryName(category.getName());
                }
            }
            
            return Result.success("获取成功", product);
        } catch (Exception e) {
            log.error("获取商品详情失败", e);
            return Result.error("获取商品详情失败");
        }
    }
    
    /**
     * 获取推荐商品
     */
    public Result<List<Product>> getRecommendProducts(Integer limit) {
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getStatus, 1)
                   .eq(Product::getIsRecommend, 1)
                   .orderByDesc(Product::getSort)
                   .orderByDesc(Product::getSales)
                   .last("LIMIT " + limit);
        
        List<Product> products = productMapper.selectList(queryWrapper);
        return Result.success("获取成功", products);
    }
    
    /**
     * 获取热销商品
     */
    public Result<List<Product>> getHotProducts(Integer limit) {
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getStatus, 1)
                   .orderByDesc(Product::getSales)
                   .orderByDesc(Product::getSort)
                   .last("LIMIT " + limit);
        
        List<Product> products = productMapper.selectList(queryWrapper);
        return Result.success("获取成功", products);
    }
    
    /**
     * 获取商品分类
     */
    public Result<List<Category>> getCategories() {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getStatus, 1)
                   .orderByAsc(Category::getSort)
                   .orderByDesc(Category::getCreateTime);
        
        List<Category> categories = categoryMapper.selectList(queryWrapper);
        return Result.success("获取成功", categories);
    }
    
    /**
     * 添加商品
     */
    public Result<String> addProduct(Product product) {
        productMapper.insert(product);
        return Result.success("添加成功");
    }
    
    /**
     * 更新商品
     */
    public Result<String> updateProduct(Product product) {
        Product existProduct = productMapper.selectById(product.getId());
        if (existProduct == null) {
            throw new BusinessException("商品不存在");
        }
        
        productMapper.updateById(product);
        return Result.success("更新成功");
    }
    
    /**
     * 删除商品
     */
    public Result<String> deleteProduct(Long id) {
        productMapper.deleteById(id);
        return Result.success("删除成功");
    }
    
    /**
     * 批量删除商品
     */
    public Result<String> batchDeleteProducts(List<Long> ids) {
        productMapper.deleteBatchIds(ids);
        return Result.success("批量删除成功");
    }
    
    /**
     * 更新商品状态
     */
    public Result<String> updateProductStatus(Long id, Integer status) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        product.setStatus(status);
        productMapper.updateById(product);
        return Result.success("状态更新成功");
    }
    
    /**
     * 批量更新商品状态
     */
    public Result<String> batchUpdateProductStatus(List<Long> ids, Integer status) {
        try {
            for (Long id : ids) {
                Product product = productMapper.selectById(id);
                if (product != null) {
                    product.setStatus(status);
                    productMapper.updateById(product);
                }
            }
            return Result.success("批量更新状态成功");
        } catch (Exception e) {
            log.error("批量更新商品状态失败", e);
            return Result.error("批量更新状态失败");
        }
    }
    
    /**
     * 更新商品库存
     */
    public Result<String> updateProductStock(Long id, Integer stock, String operation) {
        try {
            Product product = productMapper.selectById(id);
            if (product == null) {
                throw new BusinessException("商品不存在");
            }
            
            Integer currentStock = product.getStock();
            Integer newStock;
            
            switch (operation) {
                case "add":
                    newStock = currentStock + stock;
                    break;
                case "subtract":
                    newStock = Math.max(0, currentStock - stock);
                    break;
                case "set":
                    newStock = stock;
                    break;
                default:
                    throw new BusinessException("无效的操作类型");
            }
            
            product.setStock(newStock);
            productMapper.updateById(product);
            
            log.info("商品库存更新成功: 商品ID={}, 操作={}, 数量={}, 原库存={}, 新库存={}", 
                    id, operation, stock, currentStock, newStock);
            
            return Result.success("库存更新成功");
        } catch (Exception e) {
            log.error("更新商品库存失败", e);
            return Result.error("库存更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量更新商品库存
     */
    public Result<String> batchUpdateProductStock(List<Map<String, Object>> items) {
        try {
            for (Map<String, Object> item : items) {
                Long id = Long.valueOf(item.get("id").toString());
                Integer stock = Integer.valueOf(item.get("stock").toString());
                String operation = item.get("operation").toString();
                
                updateProductStock(id, stock, operation);
            }
            return Result.success("批量更新库存成功");
        } catch (Exception e) {
            log.error("批量更新商品库存失败", e);
            return Result.error("批量更新库存失败");
        }
    }
    
    /**
     * 获取库存预警商品
     */
    public Result<List<Product>> getStockAlerts(Integer threshold) {
        try {
            LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Product::getStatus, 1)
                       .le(Product::getStock, threshold)
                       .orderByAsc(Product::getStock)
                       .orderByDesc(Product::getCreateTime);
            
            List<Product> products = productMapper.selectList(queryWrapper);
            return Result.success("获取库存预警成功", products);
        } catch (Exception e) {
            log.error("获取库存预警失败", e);
            return Result.error("获取库存预警失败");
        }
    }
    
    /**
     * 获取库存统计
     */
    public Result<Map<String, Object>> getStockStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 总商品数
            LambdaQueryWrapper<Product> totalWrapper = new LambdaQueryWrapper<>();
            totalWrapper.eq(Product::getStatus, 1);
            Long totalProducts = productMapper.selectCount(totalWrapper);
            
            // 库存不足商品数（库存 <= 10）
            LambdaQueryWrapper<Product> lowStockWrapper = new LambdaQueryWrapper<>();
            lowStockWrapper.eq(Product::getStatus, 1)
                          .le(Product::getStock, 10);
            Long lowStockProducts = productMapper.selectCount(lowStockWrapper);
            
            // 零库存商品数
            LambdaQueryWrapper<Product> zeroStockWrapper = new LambdaQueryWrapper<>();
            zeroStockWrapper.eq(Product::getStatus, 1)
                           .eq(Product::getStock, 0);
            Long zeroStockProducts = productMapper.selectCount(zeroStockWrapper);
            
            // 库存充足商品数（库存 > 50）
            LambdaQueryWrapper<Product> sufficientStockWrapper = new LambdaQueryWrapper<>();
            sufficientStockWrapper.eq(Product::getStatus, 1)
                                 .gt(Product::getStock, 50);
            Long sufficientStockProducts = productMapper.selectCount(sufficientStockWrapper);
            
            statistics.put("totalProducts", totalProducts);
            statistics.put("lowStockProducts", lowStockProducts);
            statistics.put("zeroStockProducts", zeroStockProducts);
            statistics.put("sufficientStockProducts", sufficientStockProducts);
            statistics.put("lowStockRate", totalProducts > 0 ? (double) lowStockProducts / totalProducts * 100 : 0);
            
            return Result.success("获取库存统计成功", statistics);
        } catch (Exception e) {
            log.error("获取库存统计失败", e);
            return Result.error("获取库存统计失败");
        }
    }
}
