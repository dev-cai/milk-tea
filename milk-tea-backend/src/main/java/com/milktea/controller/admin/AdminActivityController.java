package com.milktea.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.milktea.common.PageResult;
import com.milktea.common.Result;
import com.milktea.entity.Activity;
import com.milktea.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 营销活动管理控制器
 */
@RestController
@RequestMapping("/admin/activity")
@RequiredArgsConstructor
public class AdminActivityController {
    
    private final ActivityService activityService;
    
    /**
     * 分页查询活动
     */
    @GetMapping("/page")
    public Result<PageResult<Activity>> page(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(required = false) String keyword) {
        Page<Activity> pageResult = activityService.page(page, size, keyword);
        return Result.success(PageResult.of(pageResult));
    }
    
    /**
     * 获取进行中的活动
     */
    @GetMapping("/active")
    public Result<List<Activity>> getActiveList() {
        List<Activity> list = activityService.getActiveList();
        return Result.success(list);
    }
    
    /**
     * 根据ID获取活动
     */
    @GetMapping("/{id}")
    public Result<Activity> getById(@PathVariable Long id) {
        Activity activity = activityService.getById(id);
        return Result.success(activity);
    }
    
    /**
     * 创建活动
     */
    @PostMapping
    public Result<String> create(@RequestBody Activity activity) {
        activityService.create(activity);
        return Result.success("创建成功", null);
    }
    
    /**
     * 更新活动
     */
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Activity activity) {
        activity.setId(id);
        activityService.update(activity);
        return Result.success("更新成功", null);
    }
    
    /**
     * 删除活动
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        activityService.delete(id);
        return Result.success("删除成功", null);
    }
    
    /**
     * 更新活动状态
     */
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestBody Activity activity) {
        activityService.updateStatus(id, activity.getStatus());
        return Result.success("状态更新成功", null);
    }
}
