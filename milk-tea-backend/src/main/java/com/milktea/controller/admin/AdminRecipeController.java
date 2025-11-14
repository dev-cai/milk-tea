package com.milktea.controller.admin;

import com.milktea.common.Result;
import com.milktea.entity.Recipe;
import com.milktea.service.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理端配方控制器
 * @author MilkTea Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/recipe")
public class AdminRecipeController {
    
    private final RecipeService recipeService;
    
    public AdminRecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }
    
    /**
     * 根据商品ID获取配方列表
     */
    @GetMapping("/product/{productId}")
    public Result<List<Recipe>> getRecipesByProductId(@PathVariable Long productId) {
        return recipeService.getRecipesByProductId(productId);
    }
    
    /**
     * 添加配方
     */
    @PostMapping
    public Result<String> addRecipe(@RequestBody Recipe recipe) {
        return recipeService.addRecipe(recipe);
    }
    
    /**
     * 更新配方
     */
    @PutMapping("/{id}")
    public Result<String> updateRecipe(@PathVariable Long id, @RequestBody Recipe recipe) {
        recipe.setId(id);
        return recipeService.updateRecipe(recipe);
    }
    
    /**
     * 删除配方
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteRecipe(@PathVariable Long id) {
        return recipeService.deleteRecipe(id);
    }
    
    /**
     * 批量更新配方
     */
    @PutMapping("/product/{productId}/batch")
    public Result<String> batchUpdateRecipes(@PathVariable Long productId, @RequestBody List<Recipe> recipes) {
        return recipeService.batchUpdateRecipes(productId, recipes);
    }
    
    /**
     * 获取配方成本分析
     */
    @GetMapping("/product/{productId}/analysis")
    public Result<Map<String, Object>> getRecipeCostAnalysis(@PathVariable Long productId) {
        return recipeService.getRecipeCostAnalysis(productId);
    }
    
    /**
     * 复制配方到其他商品
     */
    @PostMapping("/copy")
    public Result<String> copyRecipeToProduct(@RequestBody Map<String, Long> request) {
        Long fromProductId = request.get("fromProductId");
        Long toProductId = request.get("toProductId");
        return recipeService.copyRecipeToProduct(fromProductId, toProductId);
    }
}
