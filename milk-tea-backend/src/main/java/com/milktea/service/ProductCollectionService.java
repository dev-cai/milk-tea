package com.milktea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.milktea.common.Result;
import com.milktea.entity.Product;
import com.milktea.entity.ProductCollection;
import com.milktea.mapper.ProductCollectionMapper;
import com.milktea.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品收藏服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCollectionService {
    
    private final ProductCollectionMapper collectionMapper;
    private final ProductMapper productMapper;
    
    /**
     * 收藏商品
     */
    public Result<String> collectProduct(Long productId, Long userId) {
        try {
            // 检查商品是否存在
            Product product = productMapper.selectById(productId);
            if (product == null) {
                return Result.error("商品不存在");
            }
            
            // 检查是否已收藏
            LambdaQueryWrapper<ProductCollection> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProductCollection::getUserId, userId);
            wrapper.eq(ProductCollection::getProductId, productId);
            
            Long count = collectionMapper.selectCount(wrapper);
            if (count > 0) {
                return Result.error("已收藏该商品");
            }
            
            // 添加收藏
            ProductCollection collection = new ProductCollection();
            collection.setUserId(userId);
            collection.setProductId(productId);
            collectionMapper.insert(collection);
            
            log.info("收藏商品成功: userId={}, productId={}", userId, productId);
            return Result.success("收藏成功");
        } catch (Exception e) {
            log.error("收藏商品失败", e);
            return Result.error("收藏失败");
        }
    }
    
    /**
     * 取消收藏
     */
    public Result<String> uncollectProduct(Long productId, Long userId) {
        try {
            LambdaQueryWrapper<ProductCollection> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProductCollection::getUserId, userId);
            wrapper.eq(ProductCollection::getProductId, productId);
            
            collectionMapper.delete(wrapper);
            
            log.info("取消收藏成功: userId={}, productId={}", userId, productId);
            return Result.success("取消收藏成功");
        } catch (Exception e) {
            log.error("取消收藏失败", e);
            return Result.error("取消收藏失败");
        }
    }
    
    /**
     * 获取收藏列表
     */
    public Result<List<Product>> getCollections(Long userId) {
        try {
            LambdaQueryWrapper<ProductCollection> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProductCollection::getUserId, userId);
            
            List<ProductCollection> collections = collectionMapper.selectList(wrapper);
            
            // 获取商品详情
            List<Product> products = new ArrayList<>();
            for (ProductCollection collection : collections) {
                Product product = productMapper.selectById(collection.getProductId());
                if (product != null && product.getStatus() == 1) {
                    products.add(product);
                }
            }
            
            return Result.success(products);
        } catch (Exception e) {
            log.error("获取收藏列表失败", e);
            return Result.error("获取收藏列表失败");
        }
    }
    
    /**
     * 检查是否已收藏
     */
    public boolean isCollected(Long productId, Long userId) {
        LambdaQueryWrapper<ProductCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductCollection::getUserId, userId);
        wrapper.eq(ProductCollection::getProductId, productId);
        
        Long count = collectionMapper.selectCount(wrapper);
        return count > 0;
    }
}
