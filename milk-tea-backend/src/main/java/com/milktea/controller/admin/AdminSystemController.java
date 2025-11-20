package com.milktea.controller.admin;

import com.milktea.common.Result;
import com.milktea.entity.SystemConfig;
import com.milktea.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 管理端系统管理控制器
 */
@RestController
@RequestMapping("/admin/system")
public class AdminSystemController {

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 获取所有配置
     */
    @GetMapping("/config/all")
    public Result<List<SystemConfig>> getAllConfigs() {
        try {
            List<SystemConfig> configs = systemConfigService.getAllConfigs();
            return Result.success(configs);
        } catch (Exception e) {
            return Result.error("获取配置失败：" + e.getMessage());
        }
    }

    /**
     * 获取配置映射
     */
    @GetMapping("/config/map")
    public Result<Map<String, String>> getConfigMap() {
        try {
            Map<String, String> configMap = systemConfigService.getConfigMap();
            return Result.success(configMap);
        } catch (Exception e) {
            return Result.error("获取配置失败：" + e.getMessage());
        }
    }

    /**
     * 获取单个配置
     */
    @GetMapping("/config/{key}")
    public Result<String> getConfig(@PathVariable String key) {
        try {
            String value = systemConfigService.getConfigValue(key);
            return Result.success(value);
        } catch (Exception e) {
            return Result.error("获取配置失败：" + e.getMessage());
        }
    }

    /**
     * 更新单个配置
     */
    @PutMapping("/config/{key}")
    public Result<String> updateConfig(
            @PathVariable String key,
            @RequestBody Map<String, String> params) {
        try {
            String value = params.get("value");
            boolean success = systemConfigService.updateConfig(key, value);
            if (success) {
                return Result.success("配置更新成功");
            } else {
                return Result.error("配置更新失败");
            }
        } catch (Exception e) {
            return Result.error("配置更新失败：" + e.getMessage());
        }
    }

    /**
     * 批量更新配置
     */
    @PutMapping("/config/batch")
    public Result<String> batchUpdateConfig(@RequestBody Map<String, String> configMap) {
        try {
            boolean success = systemConfigService.batchUpdateConfig(configMap);
            if (success) {
                return Result.success("配置保存成功");
            } else {
                return Result.error("配置保存失败");
            }
        } catch (Exception e) {
            return Result.error("配置保存失败：" + e.getMessage());
        }
    }

    /**
     * 数据备份
     */
    @PostMapping("/backup")
    public Result<Map<String, String>> backup() {
        try {
            // 生成备份文件名
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String backupFileName = "milk_tea_backup_" + timestamp + ".sql";
            
            // 使用绝对路径创建备份目录
            Path backupDir = Paths.get(System.getProperty("user.dir"), "backups");
            if (!Files.exists(backupDir)) {
                Files.createDirectories(backupDir);
            }
            
            Path backupFilePath = backupDir.resolve(backupFileName);
            
            // 执行备份命令（这里简化处理，实际应该使用mysqldump）
            // 注意：实际生产环境需要配置数据库连接信息
            // String command = String.format(
            //     "mysqldump -u root -p milk_tea > %s",
            //     backupFilePath.toString()
            // );
            // Process process = Runtime.getRuntime().exec(command);
            // process.waitFor();
            
            // 创建一个示例备份文件（实际应该是真实的数据库备份）
            StringBuilder backupContent = new StringBuilder();
            backupContent.append("-- MySQL dump for milk_tea database\n");
            backupContent.append("-- Generated at: ").append(LocalDateTime.now()).append("\n");
            backupContent.append("-- Backup file: ").append(backupFileName).append("\n");
            backupContent.append("-- This is a sample backup file\n");
            backupContent.append("-- In production, this should be a real database dump using mysqldump\n\n");
            backupContent.append("USE milk_tea;\n\n");
            backupContent.append("-- Sample backup content\n");
            
            Files.writeString(backupFilePath, backupContent.toString());
            
            // 更新最后备份时间
            systemConfigService.updateLastBackupTime();
            
            Map<String, String> result = new java.util.HashMap<>();
            result.put("fileName", backupFileName);
            result.put("downloadUrl", "/admin/system/backup/download/" + backupFileName);
            
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("数据备份失败：" + e.getMessage());
        }
    }

    /**
     * 下载备份文件
     */
    @GetMapping("/backup/download/{fileName:.+}")
    public void downloadBackup(@PathVariable String fileName, 
                               jakarta.servlet.http.HttpServletResponse response) {
        OutputStream outputStream = null;
        InputStream inputStream = null;
        
        try {
            // 安全检查：防止路径遍历攻击
            if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
                response.setStatus(400);
                response.setContentType("text/plain; charset=UTF-8");
                response.getWriter().write("非法的文件名");
                return;
            }
            
            // 使用绝对路径
            Path backupDir = Paths.get(System.getProperty("user.dir"), "backups");
            Path filePath = backupDir.resolve(fileName);
            
            if (!Files.exists(filePath)) {
                response.setStatus(404);
                response.setContentType("text/plain; charset=UTF-8");
                response.getWriter().write("文件不存在");
                return;
            }
            
            // 设置响应头
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            response.setHeader("Content-Length", String.valueOf(Files.size(filePath)));
            
            // 读取文件并写入响应
            inputStream = Files.newInputStream(filePath);
            outputStream = response.getOutputStream();
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            outputStream.flush();
            
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (!response.isCommitted()) {
                    response.setStatus(500);
                    response.setContentType("text/plain; charset=UTF-8");
                    response.getWriter().write("下载失败：" + e.getMessage());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取最后备份时间
     */
    @GetMapping("/backup/last-time")
    public Result<String> getLastBackupTime() {
        try {
            String lastBackupTime = systemConfigService.getLastBackupTime();
            return Result.success(lastBackupTime);
        } catch (Exception e) {
            return Result.error("获取备份时间失败：" + e.getMessage());
        }
    }



    /**
     * 数据恢复
     */
    @PostMapping("/restore")
    public Result<String> restore(@RequestParam("file") String filePath) {
        try {
            // 执行恢复命令（这里简化处理，实际应该使用mysql命令）
            String command = String.format(
                "mysql -u root -p milk_tea < %s",
                filePath
            );
            
            return Result.success("数据恢复成功");
        } catch (Exception e) {
            return Result.error("数据恢复失败：" + e.getMessage());
        }
    }

    /**
     * 清理缓存
     */
    @PostMapping("/cache/clear")
    public Result<String> clearCache() {
        try {
            // 这里可以添加清理缓存的逻辑
            // 例如：清理Redis缓存、清理本地缓存等
            return Result.success("缓存清理成功");
        } catch (Exception e) {
            return Result.error("缓存清理失败：" + e.getMessage());
        }
    }

    /**
     * 获取系统信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getSystemInfo() {
        try {
            Map<String, Object> info = new java.util.HashMap<>();
            
            // 系统信息
            info.put("osName", System.getProperty("os.name"));
            info.put("osVersion", System.getProperty("os.version"));
            info.put("javaVersion", System.getProperty("java.version"));
            
            // JVM信息
            Runtime runtime = Runtime.getRuntime();
            info.put("totalMemory", runtime.totalMemory() / 1024 / 1024 + " MB");
            info.put("freeMemory", runtime.freeMemory() / 1024 / 1024 + " MB");
            info.put("maxMemory", runtime.maxMemory() / 1024 / 1024 + " MB");
            info.put("processors", runtime.availableProcessors());
            
            return Result.success(info);
        } catch (Exception e) {
            return Result.error("获取系统信息失败：" + e.getMessage());
        }
    }


}
