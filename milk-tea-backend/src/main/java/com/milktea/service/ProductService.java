package com.milktea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.milktea.common.PageResult;
import com.milktea.common.Result;
import com.milktea.entity.Category;
import com.milktea.entity.Product;
import com.milktea.exception.BusinessException;
import com.milktea.mapper.CategoryMapper;
import com.milktea.mapper.OrderItemMapper;
import com.milktea.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品服务类
 * @author MilkTea Team
 */
@Slf4j
@Service
public class ProductService {
    
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final OrderItemMapper orderItemMapper;
    
    public ProductService(ProductMapper productMapper, CategoryMapper categoryMapper, OrderItemMapper orderItemMapper) {
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
        this.orderItemMapper = orderItemMapper;
    }
    
    /**
     * 分页查询商品（用户端）
     */
    public Result<PageResult<Product>> getProductPage(Integer page, Integer size, Long categoryId, String keyword, String sortType, String sortOrder) {
        Page<Product> pageObj = new Page<>(normalizePage(page), normalizePageSize(size));
        
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getStatus, 1) // 只查询上架商品
                   .eq(Product::getDeleted, 0);
        
        if (categoryId != null) {
            queryWrapper.eq(Product::getCategoryId, categoryId);
        }
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper.like(Product::getName, keyword)
                       .or()
                       .like(Product::getDescription, keyword));
        }
        
        // 处理排序
        if (StringUtils.hasText(sortType)) {
            boolean isAsc = "asc".equalsIgnoreCase(sortOrder);
            switch (sortType) {
                case "sales":
                    // 按销量排序
                    if (isAsc) {
                        queryWrapper.orderByAsc(Product::getSales);
                    } else {
                        queryWrapper.orderByDesc(Product::getSales);
                    }
                    break;
                case "price":
                    // 按价格排序
                    if (isAsc) {
                        queryWrapper.orderByAsc(Product::getPrice);
                    } else {
                        queryWrapper.orderByDesc(Product::getPrice);
                    }
                    break;
                case "rating":
                    // 按评分排序（暂时按销量代替）
                    queryWrapper.orderByDesc(Product::getSales);
                    break;
                default:
                    // 默认排序：综合排序
                    queryWrapper.orderByDesc(Product::getIsRecommend)
                               .orderByDesc(Product::getSort)
                               .orderByDesc(Product::getSales);
                    break;
            }
        } else {
            // 默认排序
            queryWrapper.orderByDesc(Product::getSort)
                       .orderByDesc(Product::getCreateTime);
        }
        
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
        Page<Product> pageObj = new Page<>(normalizePage(page), normalizePageSize(size));
        
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
            queryWrapper.and(wrapper -> wrapper.like(Product::getName, keyword)
                       .or()
                       .like(Product::getDescription, keyword));
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
                   .last("LIMIT " + normalizeLimit(limit));
        
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
                   .last("LIMIT " + normalizeLimit(limit));
        
        List<Product> products = productMapper.selectList(queryWrapper);
        return Result.success("获取成功", products);
    }
    
    /**
     * 获取个性化推荐商品
     * 基于用户购买历史推荐同类商品，如果没有购买记录则返回热销商品
     */
    public Result<List<Product>> getPersonalizedProducts(Long userId, Integer limit) {
        try {
            int safeLimit = normalizeLimit(limit);
            // 1. 查询用户购买过的商品ID列表
            List<Long> purchasedProductIds = orderItemMapper.selectPurchasedProductIdsByUserId(userId);
            
            if (purchasedProductIds == null || purchasedProductIds.isEmpty()) {
                // 没有购买记录，返回热销商品
                log.info("用户{}没有购买记录，返回热销商品", userId);
                return getHotProducts(safeLimit);
            }
            
            // 2. 查询用户购买过的商品详情，获取分类信息
            List<Product> purchasedProducts = productMapper.selectBatchIds(purchasedProductIds);
            
            // 3. 统计用户购买最多的分类
            Map<Long, Long> categoryCountMap = purchasedProducts.stream()
                .filter(p -> p.getCategoryId() != null)
                .collect(Collectors.groupingBy(Product::getCategoryId, Collectors.counting()));
            
            // 4. 按购买次数排序，获取最喜欢的分类
            List<Long> favoriteCategories = categoryCountMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .limit(3) // 取前3个最喜欢的分类
                .collect(Collectors.toList());

            if (favoriteCategories.isEmpty()) {
                return getHotProducts(safeLimit);
            }
            
            // 5. 从这些分类中推荐商品（排除已购买的）
            LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Product::getStatus, 1)
                       .in(Product::getCategoryId, favoriteCategories)
                       .notIn(Product::getId, purchasedProductIds) // 排除已购买的商品
                       .orderByDesc(Product::getSales)
                       .orderByDesc(Product::getSort)
                       .last("LIMIT " + safeLimit);
            
            List<Product> recommendedProducts = productMapper.selectList(queryWrapper);
            
            // 6. 如果推荐的商品不够，补充热销商品
            if (recommendedProducts.size() < safeLimit) {
                int remaining = safeLimit - recommendedProducts.size();
                LambdaQueryWrapper<Product> hotWrapper = new LambdaQueryWrapper<>();
                hotWrapper.eq(Product::getStatus, 1)
                         .notIn(Product::getId, purchasedProductIds)
                         .orderByDesc(Product::getSales)
                         .last("LIMIT " + remaining);
                
                List<Product> hotProducts = productMapper.selectList(hotWrapper);
                
                // 合并结果，去重
                Set<Long> existingIds = recommendedProducts.stream()
                    .map(Product::getId)
                    .collect(Collectors.toSet());
                
                for (Product product : hotProducts) {
                    if (!existingIds.contains(product.getId())) {
                        recommendedProducts.add(product);
                        if (recommendedProducts.size() >= safeLimit) {
                            break;
                        }
                    }
                }
            }
            
            log.info("为用户{}推荐了{}个商品", userId, recommendedProducts.size());
            return Result.success("获取成功", recommendedProducts);
            
        } catch (Exception e) {
            log.error("获取个性化推荐失败，返回热销商品", e);
            // 出错时返回热销商品
            return getHotProducts(normalizeLimit(limit));
        }
    }

    private int normalizeLimit(Integer limit) {
        return limit == null ? 10 : Math.max(1, Math.min(limit, 50));
    }

    private long normalizePage(Integer page) {
        return page == null ? 1L : Math.max(1L, page.longValue());
    }

    private long normalizePageSize(Integer size) {
        return size == null ? 10L : Math.max(1L, Math.min(size.longValue(), 100L));
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
    
    /**
     * 收藏商品（暂不实现，需要添加 ProductCollectionMapper 依赖）
     */
    public Result<String> collectProduct(Long productId, Long userId) {
        // TODO: 需要在构造函数中注入 ProductCollectionMapper
        return Result.error("收藏功能暂未实现");
    }
    
    /**
     * 取消收藏（暂不实现，需要添加 ProductCollectionMapper 依赖）
     */
    public Result<String> uncollectProduct(Long productId, Long userId) {
        // TODO: 需要在构造函数中注入 ProductCollectionMapper
        return Result.error("取消收藏功能暂未实现");
    }
    
    /**
     * 获取收藏列表（暂不实现，需要添加 ProductCollectionMapper 依赖）
     */
    public Result<List<Product>> getCollections(Long userId) {
        // TODO: 需要在构造函数中注入 ProductCollectionMapper
        return Result.error("获取收藏列表功能暂未实现");
    }
}
