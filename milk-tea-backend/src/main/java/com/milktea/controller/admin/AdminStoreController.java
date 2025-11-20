package com.milktea.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.milktea.common.Result;
import com.milktea.entity.Store;
import com.milktea.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理端门店管理控制器
 */
@RestController
@RequestMapping("/admin/store")
public class AdminStoreController {

    @Autowired
    private StoreService storeService;

    /**
     * 获取门店统计信息
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        try {
            Map<String, Object> stats = storeService.getStoreStats();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取门店统计失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询门店列表
     */
    @GetMapping("/list")
    public Result<IPage<Store>> getList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        try {
            IPage<Store> pageData = storeService.getStoreList(page, size, keyword, status);
            return Result.success(pageData);
        } catch (Exception e) {
            return Result.error("查询门店列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有门店列表（不分页）
     */
    @GetMapping("/all")
    public Result<List<Store>> getAllStores() {
        try {
            List<Store> stores = storeService.getAllStores();
            return Result.success(stores);
        } catch (Exception e) {
            return Result.error("获取门店列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取门店详情
     */
    @GetMapping("/{id}")
    public Result<Store> getDetail(@PathVariable Long id) {
        try {
            Store store = storeService.getStoreById(id);
            if (store == null) {
                return Result.error("门店不存在");
            }
            return Result.success(store);
        } catch (Exception e) {
            return Result.error("获取门店详情失败：" + e.getMessage());
        }
    }

    /**
     * 添加门店
     */
    @PostMapping
    public Result<String> add(@RequestBody Store store) {
        try {
            boolean success = storeService.addStore(store);
            if (success) {
                return Result.success("门店添加成功");
            } else {
                return Result.error("门店添加失败");
            }
        } catch (Exception e) {
            return Result.error("门店添加失败：" + e.getMessage());
        }
    }

    /**
     * 更新门店信息
     */
    @PutMapping
    public Result<String> update(@RequestBody Store store) {
        try {
            boolean success = storeService.updateStore(store);
            if (success) {
                return Result.success("门店信息更新成功");
            } else {
                return Result.error("门店信息更新失败");
            }
        } catch (Exception e) {
            return Result.error("门店信息更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除门店
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        try {
            boolean success = storeService.deleteStore(id);
            if (success) {
                return Result.success("门店删除成功");
            } else {
                return Result.error("门店删除失败");
            }
        } catch (Exception e) {
            return Result.error("门店删除失败：" + e.getMessage());
        }
    }

    /**
     * 切换门店状态
     */
    @PutMapping("/{id}/toggle-status")
    public Result<String> toggleStatus(@PathVariable Long id) {
        try {
            boolean success = storeService.toggleStatus(id);
            if (success) {
                return Result.success("状态切换成功");
            } else {
                return Result.error("状态切换失败");
            }
        } catch (Exception e) {
            return Result.error("状态切换失败：" + e.getMessage());
        }
    }

    /**
     * 更新门店员工数量
     */
    @PutMapping("/{id}/staff-count")
    public Result<String> updateStaffCount(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> params) {
        try {
            Integer count = params.get("count");
            boolean success = storeService.updateStaffCount(id, count);
            if (success) {
                return Result.success("员工数量更新成功");
            } else {
                return Result.error("员工数量更新失败");
            }
        } catch (Exception e) {
            return Result.error("员工数量更新失败：" + e.getMessage());
        }
    }
}
