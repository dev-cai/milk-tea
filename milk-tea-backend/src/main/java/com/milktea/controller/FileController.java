package com.milktea.controller;

import com.milktea.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 * @author MilkTea Team
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {
    
    // 上传文件保存路径
    @Value("${file.upload.path:uploads/}")
    private String uploadPath;
    
    // 服务器地址
    @Value("${server.host:http://localhost:8080}")
    private String serverHost;
    
    /**
     * 上传头像
     */
    @PostMapping("/upload-avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            
            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("只能上传图片文件");
            }
            
            // 检查文件大小（限制2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                return Result.error("文件大小不能超过2MB");
            }
            
            // 获取原始文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            // 生成新文件名（使用UUID避免重复）
            String newFilename = "avatar_" + UUID.randomUUID().toString() + extension;
            
            // 创建上传目录
            String avatarPath = uploadPath + "avatars/";
            File uploadDir = new File(avatarPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 保存文件
            Path filePath = Paths.get(avatarPath + newFilename);
            Files.write(filePath, file.getBytes());
            
            // 返回访问URL（完整的HTTP地址）
            String fileUrl = serverHost + "/api/uploads/avatars/" + newFilename;
            
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", newFilename);
            
            log.info("头像上传成功: {}", fileUrl);
            
            return Result.success("上传成功", result);
            
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传普通图片
     */
    @PostMapping("/upload-image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            
            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("只能上传图片文件");
            }
            
            // 检查文件大小（限制5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                return Result.error("文件大小不能超过5MB");
            }
            
            // 获取原始文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            // 生成新文件名
            String newFilename = "img_" + UUID.randomUUID().toString() + extension;
            
            // 创建上传目录
            String imagePath = uploadPath + "images/";
            File uploadDir = new File(imagePath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 保存文件
            Path filePath = Paths.get(imagePath + newFilename);
            Files.write(filePath, file.getBytes());
            
            // 返回访问URL（完整的HTTP地址）
            String fileUrl = serverHost + "/api/uploads/images/" + newFilename;
            
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", newFilename);
            
            log.info("图片上传成功: {}", fileUrl);
            
            return Result.success("上传成功", result);
            
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
}
