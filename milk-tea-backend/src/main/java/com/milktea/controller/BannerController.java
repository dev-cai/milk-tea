package com.milktea.controller;

import com.milktea.common.Result;
import com.milktea.entity.Banner;
import com.milktea.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 轮播图控制器（用户端）
 */
@RestController
@RequestMapping("/banner")
@RequiredArgsConstructor
public class BannerController {
    
    private final BannerService bannerService;
    
    /**
     * 获取首页轮播图
     */
    @GetMapping("/list")
    public Result<List<Banner>> list() {
        List<Banner> list = bannerService.getActiveList();
        return Result.success(list);
    }
}
