package com.milktea.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.milktea.common.PageResult;
import com.milktea.common.Result;
import com.milktea.entity.Banner;
import com.milktea.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 轮播图管理控制器
 */
@RestController
@RequestMapping("/admin/banner")
@RequiredArgsConstructor
public class AdminBannerController {
    
    private final BannerService bannerService;
    
    /**
     * 分页查询轮播图
     */
    @GetMapping("/page")
    public Result<PageResult<Banner>> page(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        Page<Banner> pageResult = bannerService.page(page, size);
        return Result.success(PageResult.of(pageResult));
    }
    
    /**
     * 获取所有轮播图
     */
    @GetMapping("/list")
    public Result<List<Banner>> list() {
        List<Banner> list = bannerService.getActiveList();
        return Result.success(list);
    }
    
    /**
     * 根据ID获取轮播图
     */
    @GetMapping("/{id}")
    public Result<Banner> getById(@PathVariable Long id) {
        Banner banner = bannerService.getById(id);
        return Result.success(banner);
    }
    
    /**
     * 创建轮播图
     */
    @PostMapping
    public Result<String> create(@RequestBody Banner banner) {
        bannerService.create(banner);
        return Result.success("创建成功", null);
    }
    
    /**
     * 更新轮播图
     */
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Banner banner) {
        banner.setId(id);
        bannerService.update(banner);
        return Result.success("更新成功", null);
    }
    
    /**
     * 删除轮播图
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        bannerService.delete(id);
        return Result.success("删除成功", null);
    }
    
    /**
     * 更新轮播图状态
     */
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestBody Banner banner) {
        bannerService.updateStatus(id, banner.getStatus());
        return Result.success("状态更新成功", null);
    }
}
