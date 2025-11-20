package com.milktea.controller.admin;

import com.milktea.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 管理端文件上传控制器
 * @author MilkTea Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/upload")
public class AdminUploadController {
    
    @Value("${file.upload.path:uploads/}")
    private String uploadPath;
    
    @Value("${file.upload.url-prefix:/uploads/}")
    private String urlPrefix;
    
    /**
     * 测试上传接口
     */
    @GetMapping("/test")
    public Result<String> testUpload() {
        return Result.success("上传接口正常");
    }
    
    /**
     * 上传图片
     */
    @PostMapping("/image")
    public Result<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        log.info("开始处理图片上传，文件名: {}, 大小: {}", file.getOriginalFilename(), file.getSize());
        try {
            if (file.isEmpty()) {
                log.warn("上传文件为空");
                return Result.error("上传文件不能为空");
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.startsWith("image/"))) {
                return Result.error("只能上传图片文件");
            }
            
            // 验证文件大小（2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                return Result.error("文件大小不能超过2MB");
            }
            
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String fileName = UUID.randomUUID().toString() + extension;
            
            // 按日期创建目录
            String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String relativePath = "images/" + dateDir + "/" + fileName;
            
            // 创建完整路径
            File uploadDir = new File(uploadPath + "images/" + dateDir);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 保存文件
            File destFile = new File(uploadPath + relativePath);
            file.transferTo(destFile);
            
            // 返回访问URL（前端通过/api代理访问）
            String url = "/api" + urlPrefix + relativePath;
            
            Map<String, Object> result = new HashMap<>();
            result.put("url", url);
            result.put("filename", fileName);
            result.put("size", file.getSize());
            
            log.info("图片上传成功: {}", url);
            return Result.success("上传成功", result);
            
        } catch (IOException e) {
            log.error("图片上传失败", e);
            return Result.error("上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传文件
     */
    @PostMapping("/file")
    public Result<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("上传文件不能为空");
            }
            
            // 验证文件大小（10MB）
            if (file.getSize() > 10 * 1024 * 1024) {
                return Result.error("文件大小不能超过10MB");
            }
            
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String fileName = UUID.randomUUID().toString() + extension;
            
            // 按日期创建目录
            String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String relativePath = "files/" + dateDir + "/" + fileName;
            
            // 创建完整路径
            File uploadDir = new File(uploadPath + "files/" + dateDir);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 保存文件
            File destFile = new File(uploadPath + relativePath);
            file.transferTo(destFile);
            
            // 返回访问URL（前端通过/api代理访问）
            String url = "/api" + urlPrefix + relativePath;
            
            Map<String, Object> result = new HashMap<>();
            result.put("url", url);
            result.put("filename", originalFilename);
            result.put("size", file.getSize());
            
            log.info("文件上传成功: {}", url);
            return Result.success("上传成功", result);
            
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}
