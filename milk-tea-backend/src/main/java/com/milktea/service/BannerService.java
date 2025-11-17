package com.milktea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.milktea.entity.Banner;
import com.milktea.mapper.BannerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 轮播图服务类
 */
@Service
@RequiredArgsConstructor
public class BannerService {
    
    private final BannerMapper bannerMapper;
    
    /**
     * 分页查询轮播图
     */
    public Page<Banner> page(int page, int size) {
        Page<Banner> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Banner::getSort);
        return bannerMapper.selectPage(pageParam, wrapper);
    }
    
    /**
     * 获取所有启用的轮播图
     */
    public List<Banner> getActiveList() {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Banner::getStatus, 1);
        wrapper.orderByAsc(Banner::getSort);
        return bannerMapper.selectList(wrapper);
    }
    
    /**
     * 根据ID获取轮播图
     */
    public Banner getById(Long id) {
        return bannerMapper.selectById(id);
    }
    
    /**
     * 创建轮播图
     */
    public void create(Banner banner) {
        bannerMapper.insert(banner);
    }
    
    /**
     * 更新轮播图
     */
    public void update(Banner banner) {
        bannerMapper.updateById(banner);
    }
    
    /**
     * 删除轮播图
     */
    public void delete(Long id) {
        bannerMapper.deleteById(id);
    }
    
    /**
     * 更新状态
     */
    public void updateStatus(Long id, Integer status) {
        Banner banner = new Banner();
        banner.setId(id);
        banner.setStatus(status);
        bannerMapper.updateById(banner);
    }
}
