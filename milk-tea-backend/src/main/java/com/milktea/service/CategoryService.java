package com.milktea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.milktea.common.Result;
import com.milktea.entity.Category;
import com.milktea.mapper.CategoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品分类服务类
 * @author MilkTea Team
 */
@Slf4j
@Service
public class CategoryService {
    
    private final CategoryMapper categoryMapper;
    
    public CategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }
    
    /**
     * 获取所有分类（管理端）
     */
    public Result<List<Category>> getAllCategories() {
        try {
            LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Category::getDeleted, 0) // 查询所有未删除的分类
                       .orderByAsc(Category::getSort)
                       .orderByDesc(Category::getCreateTime);
            
            List<Category> categories = categoryMapper.selectList(queryWrapper);
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取分类列表失败", e);
            return Result.error("获取分类列表失败");
        }
    }
    
    /**
     * 获取分类树结构
     */
    public Result<List<Category>> getCategoryTree() {
        try {
            LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Category::getDeleted, 0)
                       .orderByAsc(Category::getSort)
                       .orderByDesc(Category::getCreateTime);
            
            List<Category> categories = categoryMapper.selectList(queryWrapper);
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取分类树失败", e);
            return Result.error("获取分类树失败");
        }
    }
    
    /**
     * 更新分类状态
     */
    public Result<String> updateCategoryStatus(Long id, Integer status) {
        try {
            Category category = categoryMapper.selectById(id);
            if (category == null) {
                return Result.error("分类不存在");
            }
            
            category.setStatus(status);
            categoryMapper.updateById(category);
            return Result.success("更新状态成功");
        } catch (Exception e) {
            log.error("更新分类状态失败", e);
            return Result.error("更新分类状态失败");
        }
    }
    
    /**
     * 根据ID获取分类
     */
    public Result<Category> getCategoryById(Long id) {
        try {
            Category category = categoryMapper.selectById(id);
            if (category == null) {
                return Result.error("分类不存在");
            }
            return Result.success(category);
        } catch (Exception e) {
            log.error("获取分类详情失败", e);
            return Result.error("获取分类详情失败");
        }
    }
    
    /**
     * 添加分类
     */
    public Result<String> addCategory(Category category) {
        try {
            categoryMapper.insert(category);
            return Result.success("添加分类成功");
        } catch (Exception e) {
            log.error("添加分类失败", e);
            return Result.error("添加分类失败");
        }
    }
    
    /**
     * 更新分类
     */
    public Result<String> updateCategory(Category category) {
        try {
            categoryMapper.updateById(category);
            return Result.success("更新分类成功");
        } catch (Exception e) {
            log.error("更新分类失败", e);
            return Result.error("更新分类失败");
        }
    }
    
    /**
     * 删除分类
     */
    public Result<String> deleteCategory(Long id) {
        try {
            categoryMapper.deleteById(id);
            return Result.success("删除分类成功");
        } catch (Exception e) {
            log.error("删除分类失败", e);
            return Result.error("删除分类失败");
        }
    }
}
