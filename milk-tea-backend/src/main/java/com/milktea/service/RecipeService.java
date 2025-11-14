package com.milktea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.milktea.common.Result;
import com.milktea.entity.Product;
import com.milktea.entity.Recipe;
import com.milktea.mapper.ProductMapper;
import com.milktea.mapper.RecipeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配方服务类
 * @author MilkTea Team
 */
@Slf4j
@Service
public class RecipeService {
    
    private final RecipeMapper recipeMapper;
    private final ProductMapper productMapper;
    
    public RecipeService(RecipeMapper recipeMapper, ProductMapper productMapper) {
        this.recipeMapper = recipeMapper;
        this.productMapper = productMapper;
    }
    
    /**
     * 根据商品ID获取配方列表
     */
    public Result<List<Recipe>> getRecipesByProductId(Long productId) {
        try {
            LambdaQueryWrapper<Recipe> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Recipe::getProductId, productId)
                       .orderByAsc(Recipe::getSort)
                       .orderByDesc(Recipe::getCreateTime);
            
            List<Recipe> recipes = recipeMapper.selectList(queryWrapper);
            return Result.success("获取配方成功", recipes);
        } catch (Exception e) {
            log.error("获取配方失败", e);
            return Result.error("获取配方失败");
        }
    }
    
    /**
     * 添加配方
     */
    public Result<String> addRecipe(Recipe recipe) {
        try {
            // 计算总成本
            if (recipe.getQuantity() != null && recipe.getUnitCost() != null) {
                recipe.setTotalCost(recipe.getQuantity().multiply(recipe.getUnitCost()));
            }
            
            recipeMapper.insert(recipe);
            
            // 更新商品成本
            updateProductCost(recipe.getProductId());
            
            return Result.success("添加配方成功");
        } catch (Exception e) {
            log.error("添加配方失败", e);
            return Result.error("添加配方失败");
        }
    }
    
    /**
     * 更新配方
     */
    public Result<String> updateRecipe(Recipe recipe) {
        try {
            // 计算总成本
            if (recipe.getQuantity() != null && recipe.getUnitCost() != null) {
                recipe.setTotalCost(recipe.getQuantity().multiply(recipe.getUnitCost()));
            }
            
            recipeMapper.updateById(recipe);
            
            // 更新商品成本
            updateProductCost(recipe.getProductId());
            
            return Result.success("更新配方成功");
        } catch (Exception e) {
            log.error("更新配方失败", e);
            return Result.error("更新配方失败");
        }
    }
    
    /**
     * 删除配方
     */
    public Result<String> deleteRecipe(Long id) {
        try {
            Recipe recipe = recipeMapper.selectById(id);
            if (recipe == null) {
                return Result.error("配方不存在");
            }
            
            Long productId = recipe.getProductId();
            recipeMapper.deleteById(id);
            
            // 更新商品成本
            updateProductCost(productId);
            
            return Result.success("删除配方成功");
        } catch (Exception e) {
            log.error("删除配方失败", e);
            return Result.error("删除配方失败");
        }
    }
    
    /**
     * 批量更新配方
     */
    @Transactional
    public Result<String> batchUpdateRecipes(Long productId, List<Recipe> recipes) {
        try {
            // 删除原有配方
            LambdaQueryWrapper<Recipe> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(Recipe::getProductId, productId);
            recipeMapper.delete(deleteWrapper);
            
            // 添加新配方
            for (Recipe recipe : recipes) {
                recipe.setProductId(productId);
                if (recipe.getQuantity() != null && recipe.getUnitCost() != null) {
                    recipe.setTotalCost(recipe.getQuantity().multiply(recipe.getUnitCost()));
                }
                recipeMapper.insert(recipe);
            }
            
            // 更新商品成本
            updateProductCost(productId);
            
            return Result.success("批量更新配方成功");
        } catch (Exception e) {
            log.error("批量更新配方失败", e);
            return Result.error("批量更新配方失败");
        }
    }
    
    /**
     * 获取配方成本分析
     */
    public Result<Map<String, Object>> getRecipeCostAnalysis(Long productId) {
        try {
            Map<String, Object> analysis = new HashMap<>();
            
            LambdaQueryWrapper<Recipe> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Recipe::getProductId, productId);
            List<Recipe> recipes = recipeMapper.selectList(queryWrapper);
            
            BigDecimal totalCost = BigDecimal.ZERO;
            for (Recipe recipe : recipes) {
                if (recipe.getTotalCost() != null) {
                    totalCost = totalCost.add(recipe.getTotalCost());
                }
            }
            
            Product product = productMapper.selectById(productId);
            if (product != null) {
                analysis.put("productName", product.getName());
                analysis.put("sellingPrice", product.getPrice());
                analysis.put("totalCost", totalCost);
                analysis.put("profit", product.getPrice().subtract(totalCost));
                analysis.put("profitMargin", 
                    product.getPrice().compareTo(BigDecimal.ZERO) > 0 ? 
                    product.getPrice().subtract(totalCost).divide(product.getPrice(), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")) : 
                    BigDecimal.ZERO);
            }
            
            analysis.put("recipes", recipes);
            analysis.put("recipeCount", recipes.size());
            
            return Result.success("获取成本分析成功", analysis);
        } catch (Exception e) {
            log.error("获取配方成本分析失败", e);
            return Result.error("获取成本分析失败");
        }
    }
    
    /**
     * 复制配方到其他商品
     */
    @Transactional
    public Result<String> copyRecipeToProduct(Long fromProductId, Long toProductId) {
        try {
            LambdaQueryWrapper<Recipe> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Recipe::getProductId, fromProductId);
            List<Recipe> sourceRecipes = recipeMapper.selectList(queryWrapper);
            
            for (Recipe recipe : sourceRecipes) {
                Recipe newRecipe = new Recipe();
                newRecipe.setProductId(toProductId);
                newRecipe.setIngredientName(recipe.getIngredientName());
                newRecipe.setQuantity(recipe.getQuantity());
                newRecipe.setUnit(recipe.getUnit());
                newRecipe.setUnitCost(recipe.getUnitCost());
                newRecipe.setTotalCost(recipe.getTotalCost());
                newRecipe.setDescription(recipe.getDescription());
                newRecipe.setSort(recipe.getSort());
                
                recipeMapper.insert(newRecipe);
            }
            
            // 更新目标商品成本
            updateProductCost(toProductId);
            
            return Result.success("复制配方成功");
        } catch (Exception e) {
            log.error("复制配方失败", e);
            return Result.error("复制配方失败");
        }
    }
    
    /**
     * 更新商品成本
     */
    private void updateProductCost(Long productId) {
        try {
            LambdaQueryWrapper<Recipe> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Recipe::getProductId, productId);
            List<Recipe> recipes = recipeMapper.selectList(queryWrapper);
            
            BigDecimal totalCost = BigDecimal.ZERO;
            for (Recipe recipe : recipes) {
                if (recipe.getTotalCost() != null) {
                    totalCost = totalCost.add(recipe.getTotalCost());
                }
            }
            
            Product product = productMapper.selectById(productId);
            if (product != null) {
                product.setCost(totalCost);
                productMapper.updateById(product);
                log.info("更新商品成本: 商品ID={}, 成本={}", productId, totalCost);
            }
        } catch (Exception e) {
            log.error("更新商品成本失败", e);
        }
    }
}
