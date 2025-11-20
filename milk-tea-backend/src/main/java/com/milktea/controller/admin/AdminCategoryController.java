package com.milktea.controller.admin;

import com.milktea.common.Result;
import com.milktea.entity.Category;
import com.milktea.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理端分类控制器
 * @author MilkTea Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/category")
public class AdminCategoryController {
    
    private final CategoryService categoryService;
    
    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    /**
     * 获取所有分类
     */
    @GetMapping("/list")
    public Result<List<Category>> getAllCategories() {
        return categoryService.getAllCategories();
    }
    
    /**
     * 根据ID获取分类
     */
    @GetMapping("/{id}")
    public Result<Category> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }
    
    /**
     * 添加分类
     */
    @PostMapping
    public Result<String> addCategory(@RequestBody Category category) {
        return categoryService.addCategory(category);
    }
    
    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public Result<String> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        return categoryService.updateCategory(category);
    }
    
    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteCategory(@PathVariable Long id) {
        return categoryService.deleteCategory(id);
    }
    
    /**
     * 获取分类树
     */
    @GetMapping("/tree")
    public Result<List<Category>> getCategoryTree() {
        return categoryService.getCategoryTree();
    }
    
    /**
     * 更新分类状态
     */
    @PutMapping("/{id}/status")
    public Result<String> updateCategoryStatus(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        Integer status = request.get("status");
        return categoryService.updateCategoryStatus(id, status);
    }
}
