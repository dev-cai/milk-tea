package com.milktea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.milktea.entity.Store;
import com.milktea.mapper.StoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 门店服务类
 */
@Service
public class StoreService {

    @Autowired
    private StoreMapper storeMapper;

    /**
     * 分页查询门店列表
     */
    public IPage<Store> getStoreList(int page, int size, String keyword, Integer status) {
        Page<Store> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        
        // 关键字搜索（门店名称或地址）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Store::getName, keyword)
                    .or()
                    .like(Store::getAddress, keyword)
                    .or()
                    .like(Store::getManager, keyword));
        }
        
        // 状态筛选
        if (status != null) {
            wrapper.eq(Store::getStatus, status);
        }
        
        wrapper.orderByDesc(Store::getCreateTime);
        
        return storeMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 获取所有门店列表（不分页）
     */
    public List<Store> getAllStores() {
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Store::getStatus, 1);
        wrapper.orderByDesc(Store::getCreateTime);
        return storeMapper.selectList(wrapper);
    }

    /**
     * 获取门店统计信息
     */
    public Map<String, Object> getStoreStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 总门店数
        Long total = storeMapper.selectCount(null);
        stats.put("total", total);
        
        // 营业中门店数
        Long active = storeMapper.selectCount(new LambdaQueryWrapper<Store>()
                .eq(Store::getStatus, 1));
        stats.put("active", active);
        
        // 关闭门店数
        Long closed = storeMapper.selectCount(new LambdaQueryWrapper<Store>()
                .eq(Store::getStatus, 0));
        stats.put("closed", closed);
        
        // 总员工数（所有门店的员工数量之和）
        List<Store> stores = storeMapper.selectList(null);
        int totalStaff = stores.stream()
                .mapToInt(store -> store.getStaffCount() != null ? store.getStaffCount() : 0)
                .sum();
        stats.put("totalStaff", totalStaff);
        
        return stats;
    }

    /**
     * 根据ID获取门店详情
     */
    public Store getStoreById(Long id) {
        return storeMapper.selectById(id);
    }

    /**
     * 添加门店
     */
    public boolean addStore(Store store) {
        // 检查门店名称是否已存在
        Long count = storeMapper.selectCount(new LambdaQueryWrapper<Store>()
                .eq(Store::getName, store.getName()));
        if (count > 0) {
            throw new RuntimeException("门店名称已存在");
        }
        
        // 设置默认状态
        if (store.getStatus() == null) {
            store.setStatus(1);
        }
        
        // 设置默认员工数量
        if (store.getStaffCount() == null) {
            store.setStaffCount(0);
        }
        
        return storeMapper.insert(store) > 0;
    }

    /**
     * 更新门店信息
     */
    public boolean updateStore(Store store) {
        Store existingStore = storeMapper.selectById(store.getId());
        if (existingStore == null) {
            throw new RuntimeException("门店不存在");
        }
        
        // 如果修改了门店名称，检查是否重复
        if (StringUtils.hasText(store.getName()) 
                && !store.getName().equals(existingStore.getName())) {
            Long count = storeMapper.selectCount(new LambdaQueryWrapper<Store>()
                    .eq(Store::getName, store.getName())
                    .ne(Store::getId, store.getId()));
            if (count > 0) {
                throw new RuntimeException("门店名称已存在");
            }
        }
        
        return storeMapper.updateById(store) > 0;
    }

    /**
     * 删除门店（逻辑删除）
     */
    public boolean deleteStore(Long id) {
        Store store = storeMapper.selectById(id);
        if (store == null) {
            throw new RuntimeException("门店不存在");
        }
        
        // 检查门店是否有员工
        if (store.getStaffCount() != null && store.getStaffCount() > 0) {
            throw new RuntimeException("该门店还有员工，无法删除");
        }
        
        return storeMapper.deleteById(id) > 0;
    }

    /**
     * 切换门店状态
     */
    public boolean toggleStatus(Long id) {
        Store store = storeMapper.selectById(id);
        if (store == null) {
            throw new RuntimeException("门店不存在");
        }
        
        store.setStatus(store.getStatus() == 1 ? 0 : 1);
        return storeMapper.updateById(store) > 0;
    }

    /**
     * 更新门店员工数量
     */
    public boolean updateStaffCount(Long id, Integer count) {
        Store store = storeMapper.selectById(id);
        if (store == null) {
            throw new RuntimeException("门店不存在");
        }
        
        store.setStaffCount(count);
        return storeMapper.updateById(store) > 0;
    }
}
